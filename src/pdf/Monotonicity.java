package pdf;

/**
 *
 * @author diaz
 */
public enum Monotonicity {

    FORCE_GROWS_AS_TAU,     /* force grows when tau increases */
    FORCE_GROWS_AS_INV_TAU, /* force grows when tau decreases */
    NON_MONOTONE            /* force is non-monotone wrt tau  */

}
