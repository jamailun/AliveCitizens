package fr.jamailun.alivecitizens.farmers;

/**
 * The differents options for a farmer
 */
public enum FarmerBehaviour {
	
	UNDEFINED,
	
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
	;
	
	
	public static FarmerBehaviour theoricalBehaviour(long worldTime) {
		// 0 = matin
		// 6K = zenith
		// 12K = crépuscule.
		// 18K = minuit
		// 23K = aurore
		
		if(worldTime > 12000)
			return SLEEP;
		if(worldTime < 1000)
			return WAKE_UP;
		if(worldTime < 5500)
			return VILLAGE_EXPLORE;
		if(worldTime < 11000)
			return WORK;
		return GO_BACK_HOME;
	}
	
}
