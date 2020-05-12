package plugin.hero;

import java.util.*;
import java.util.concurrent.*;

import org.apache.commons.math3.stat.descriptive.*;

import com.javaflair.pokerprophesier.api.card.*;
import com.javaflair.pokerprophesier.api.helper.*;
import com.javaflair.pokerprophesier.client.*;

import core.*;
import core.tasks.*;
import gui.prueckl.draw.*;

/**
 * this class represent the core of al hero plugins. As a core class, this class dependes of anothers classes in order
 * to build a useful player agent. the followin is a list of class from where the information is retrived, and the
 * actions are performed.
 * <ul>
 * <li>{@link PokerSimulator} - get the numerical values for decition making
 * <li>{@link RobotActuator} - permorm the action sended by this class.
 * 
 * *
 * <li>{@link SensorsArray} - perform visual operation of the enviorement
 * 
 * 
 * @author terry
 *
 */
public class Trooper implements Runnable {

	private PokerSimulator pokerSimulator;
	private RobotActuator robotActuator;
	private SensorsArray sensorsArray;
	private boolean isTestMode;
	private boolean isTakingSamples;
	private boolean isRunning;

	private Vector<String> availableActions;
	private int countdown;
	private Future myFuture;

	public Trooper() {
		this.pokerSimulator = new PokerSimulator();
		this.robotActuator = new RobotActuator();
		this.sensorsArray = new SensorsArray(pokerSimulator);
		this.availableActions = new Vector();
		this.outGameStats = new DescriptiveStatistics(10);
	}

	private long time1;
	private DescriptiveStatistics outGameStats;

	public void clearEnviorement() {
		sensorsArray.init();
		pokerSimulator.init();

		// at first time execution, a standar time of 10 second is used
		long tt = time1 == 0 ? 10000 : System.currentTimeMillis() - time1;
		outGameStats.addValue(tt);
		time1 = System.currentTimeMillis();
		Hero.logGame("Game play time average=" + TStringUtils.formatSpeed((long) outGameStats.getMean()));
	}

	public PokerSimulator getPokerSimulator() {
		return pokerSimulator;
	}
	/**
	 * Return the expectation of the pot odd against the <code>val</code> argument. This method cosider:
	 * <ul>
	 * <li>for negative <code>val</code> argument, this method will return negative expectative in order to ensure fold
	 * action
	 * <li>for pot value = 0 (initial bet) this method return 0 expectative
	 * <li>for val = 0 (check) this method return 0 expectative
	 * </ul>
	 * <h5>MoP page 54</h5>
	 * 
	 * @param pot - current pot amount
	 * @param val - cost of call/bet
	 * 
	 * @return expected pot odd
	 */
	public double getPotOdds(int val) {
		int pot = pokerSimulator.getPotValue() + getVillansCall();

		/*
		 * rule: i came here to win money, and for win money i need to stay in the game. for that i choose the hihgest
		 * available probability. it can be from inprove probability or from the global winning probability
		 */
		MyHandStatsHelper myhsh = pokerSimulator.getMyHandStatsHelper();
		float inprove = myhsh == null ? 0 : myhsh.getTotalProb();
		float actual = pokerSimulator.getMyGameStatsHelper().getWinProb();
		float totp = inprove > actual ? inprove : actual;
		String pnam = inprove > actual ? "improve" : "win";

		// MoP page 54
		double poto = totp * pot - val;
		// for pot=0 (initial bet) return 0 expectaive
		poto = (pot == 0) ? 0 : poto;
		// for val=0 (check) return 0 expectaive
		poto = (val == 0) ? 0 : poto;
		// for val=-1 (posible error) return -1 expectaive
		poto = (val < 0) ? -1 : poto;
		Hero.logGame(pnam + " prob=" + totp + " pot=" + pot + " value=" + val + " potodd=" + poto);
		return poto;
	}

	public RobotActuator getRobotActuator() {
		return robotActuator;
	}

	public SensorsArray getSensorsArray() {
		return sensorsArray;
	}

	public boolean isCallEnabled() {
		return sensorsArray.getScreenSensor("call").isEnabled();
	}

	public boolean isRaiseDownEnabled() {
		return sensorsArray.getScreenSensor("raise_down").isEnabled();
	}

	public boolean isRaiseEnabled() {
		return sensorsArray.getScreenSensor("raise").isEnabled();
	}

	public boolean isRaiseUpEnabled() {
		return sensorsArray.getScreenSensor("raise_up").isEnabled();
	}

	public boolean isRunning() {
		return isRunning;
	}

	public boolean isTakingSamples() {
		return isTakingSamples;
	}

	public boolean isTestMode() {
		return isTestMode;
	}

	/**
	 * This method is invoked during the idle phase (after {@link #act()} and before {@link #decide()}. use this method
	 * to perform large computations.
	 */
	protected void think() {
		// read all the table only for warm up the sensors hoping that many of then will not change in the near future
		// sensorsArray.read();
		// 191020: ayer ya la implementacion por omision jugo una partida completa y estuvo a punto de vencer a la
		// chatarra de Texas poker - poker holdem. A punto de vencer porque jugaba tan lento que me aburri del sueno :D
	}
	/**
	 * decide de action(s) to perform. This method is called when the {@link Trooper} detect that is my turn to play. At
	 * this point, the game enviorement is waiting for an accion. This method must report all posible actions using the
	 * {@link #addAction(String)} method
	 */
	private void decide() {
		sensorsArray.read("my_card1", "my_card2", "flop1", "flop2", "flop3", "turn", "river");
		if (pokerSimulator.getCurrentRound() > PokerSimulator.NO_CARDS_DEALT) {
			addAction("fold");
			addPotOddActions();
		}
	}

	/**
	 * add the action to the list of available actions. This metodh ensure:
	 * <ul>
	 * <li>the <code>fold</code> action is the only action on the list.
	 * <li>only distict actions are present in the list.</li>
	 * 
	 * @param act - action
	 */
	private void addAction(String act) {
		// if the action is fold or the list already contain a fold action
		if (act.equals("fold") || availableActions.contains("fold")) {
			availableActions.clear();
		}
		availableActions.remove(act);
		availableActions.add(act);
	}

	private boolean isMyTurnToPlay() {
		return sensorsArray.getScreenSensor("fold").isEnabled() || sensorsArray.getScreenSensor("call").isEnabled()
				|| sensorsArray.getScreenSensor("raise").isEnabled();
	}

	/**
	 * use de actions stored in {@link #availableActions} list. At this point, the game table is waiting for the herro
	 * action.
	 * <p>
	 * this implenentation randomly select an action from the list and perfom it. if <code>fold</code> action is in the
	 * list, this bust be the only action.
	 */
	protected void act() {
		if (isMyTurnToPlay() && availableActions.size() > 0) {
			final StringBuffer actl = new StringBuffer();
			availableActions.stream().forEach((act) -> actl.append(act + ", "));
			Hero.logDebug("Available actions to perform: " + actl.substring(0, actl.length() - 2));
			Hero.logGame("Current hand: " + pokerSimulator.getMyHandHelper().getHand().toString());
			if (availableActions.size() == 1) {
				robotActuator.perform(availableActions.elementAt(0));
			} else {
				double rnd = Math.random();
				int r = (int) (rnd * availableActions.size());
				robotActuator.perform(availableActions.elementAt(r));
			}
		}
	}

	@Override
	public void run() {

		while (isRunning) {
			try {

				// countdown before start
				if (countdown > 0) {
					countdown--;
					Hero.logGame("Seconds to start: " + countdown);
					Thread.sleep(1000);
					continue;
				}

				sensorsArray.lookTable();

				// look the continue button and perform the action if available.
//				sensorsArray.lookTable("continue");
				ScreenSensor ss = sensorsArray.getScreenSensor("continue");
				if (ss.isEnabled()) {
					robotActuator.perform("continue");
					clearEnviorement();
					continue;
				}

				// look the standar actions buttons. this standar button indicate that the game is waiting for my play
//				sensorsArray.lookTable("fold", "call", "raise");
				if (isMyTurnToPlay()) {
					Hero.logDebug("Deciding ...");
					decide();
					Hero.logDebug("-------------------");
					Hero.logDebug("Acting ...");
					act();
					Hero.logDebug("-------------------");
				}
				Hero.logDebug("Thinkin ...");
				think();
				Hero.logDebug("-------------------");

				// check simulator status: in case of any error, try to clean the simulator and wait for the next cycle
				if (pokerSimulator.getException() != null) {
					// clearEnviorement();
					// pokerSimulator.init();
					// continue;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * set the enviorement. this method create a new enviorement discarting all previous created objects
	 * 
	 * @param dpanel
	 */
	public void setEnviorement(DrawingPanel dpanel) {
		sensorsArray.createSensorsArray(dpanel);
		robotActuator.setEnviorement(dpanel);
		pokerSimulator.init();
	}

	public void setTakingSamples(boolean isTakingSamples) {
		this.isTakingSamples = isTakingSamples;
	}
	public void setTestMode(boolean isTestMode) {
		this.isTestMode = isTestMode;
	}
	public void start() {
		isRunning = true;
		this.countdown = 5;
		myFuture = TTaskManager.executeTask(this);
	}
	public void stop() {
		isRunning = false;
		if (myFuture != null) {
			myFuture.cancel(true);
		}
	}
	/**
	 * return <code>true</code> if the herro cards are inside of the predefinde hand distributions for pre-flop
	 * 
	 */
	private boolean isGoodHand() {
		boolean ok = false;

		// suited hand
		if (pokerSimulator.getMyHoleCards().isSuited()) {
			Hero.logGame("Hero hand: is Suited");
			ok = true;
		}

		// 10 or higher
		Card[] heroc = pokerSimulator.getMyHoleCards().getCards();
		if (heroc[0].getRank() > Card.TEN && heroc[1].getRank() > Card.TEN) {
			Hero.logGame("Hero hand: 10 or higher");
			ok = true;
		}

		// posible straight: cernters cards separated only by 1 or 2 cards provides de best probabilities (>=6%)
		if (pokerSimulator.getMyHandStatsHelper().getStraightProb() >= 6) {
			Hero.logGame("Hero hand: Posible straight");
			ok = true;
		}
		// is already a pair
		if (pokerSimulator.getMyHandHelper().isPocketPair()) {
			Hero.logGame("Hero hand: is pocket pair");
			ok = true;
		}

		if (ok) {
			Hero.logGame("the current hand is a bad hand");
		}

		return ok;
	}
	/**
	 * Compute the actions available according to {@link #getPotOdds(int)} evaluations. The resulting computation will
	 * be reflected in a single (fold) or multiples (check/call, raise, ...) actions available to be randomy perform.
	 * 
	 */
	private void addPotOddActions() {
		sensorsArray.read("pot", "call", "raise");
		int call = pokerSimulator.getCallValue();
		int raise = pokerSimulator.getRaiseValue();

		if (getPotOdds(call) >= 0) {
			addAction("call");
		}
		if (getPotOdds(raise) >= 0) {
			addAction("raise");
		}
		// TODO: compute more raise actions until potodd < 0
	}

	/**
	 * Temporal: return the sum of all villans call. used to retrive the exact amount of pot
	 * 
	 * @return
	 */
	private int getVillansCall() {
		sensorsArray.read("villan1.call", "villan2.call", "villan3.call", "villan4.call");
		int villanscall = 0;
		String val = "";
		try {
			val = sensorsArray.getScreenSensor("villan1.call").getOCR();
			villanscall += Integer.parseInt(val == null ? "0" : val);
			val = sensorsArray.getScreenSensor("villan2.call").getOCR();
			villanscall += Integer.parseInt(val == null ? "0" : val);
			val = sensorsArray.getScreenSensor("villan3.call").getOCR();
			villanscall += Integer.parseInt(val == null ? "0" : val);
			val = sensorsArray.getScreenSensor("villan4.call").getOCR();
			villanscall += Integer.parseInt(val == null ? "0" : val);
		} catch (Exception e) {
			System.err.println("Error setting villans call values.");
		}

		return villanscall;
	}
}
