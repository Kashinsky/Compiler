package cis463.fsm;

/*
 * Run a FSM on a set of states, doing state transitions based
 * on the current state
 */
public class FSM {

    private FSMState init; // the initial FSM state

    public FSM(FSMState init) {
	this.init = init;
    }

    public void run() {
	FSM.run(init);
    }

    public static void run(FSMState state) {
	while (state != null)
	    state = state.next();
    }
}
