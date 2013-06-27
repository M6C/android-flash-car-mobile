package flash.car.mobile;

public class ClientCredentials {
	  /** Value of the "API key" shown under "Simple API Access". */
	  public static final String KEY = null;

	  public static void errorIfNotSpecified() {
	    if (KEY == null) {
	      System.err.println("Please enter your API key in " + ClientCredentials.class);
	      System.exit(1);
	    }
	  }
}
