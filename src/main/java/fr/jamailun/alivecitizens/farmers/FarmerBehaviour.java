package fr.jamailun.alivecitizens.farmers;

/**
 * The differents options for a farmer
 */
public enum FarmerBehaviour {
	
	/**
	 * During night, stays at home and sleep
	 */
	SLEEP,
	
	/**
	 * The morning, stays in the house and do stuff
	 */
	WAKE_UP,
	
	/**
	 * At noon, go to the village and speak with other NPCs
	 */
	VILLAGE_EXPLORE,
	
	/**
	 * The afternoon, go and work
	 */
	WORK,
	
	/**
	 * Go back home after the work
	 */
	GO_BACK_HOME
	
}
