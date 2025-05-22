package namecorp.camera_lock_on.util;
import java.util.function.Function;

/**
 * A simple cache that holds only one value per time.
 * Useful for caching values that are expensive to compute
 * but you don't want to consume too much memory.
 * @param <TKey> The type of the key to be cached.
 * @param <TValue> The type of the value to be cached.
 */
public class SingleCache<TKey, TValue> {
    private TKey key = null;
    public TValue value = null;

    public TValue get(TKey vec, Function<TKey, TValue> valueSupplier) {
        if (key != null && key.equals(vec)) return value;
        key = vec;
        value = valueSupplier.apply(vec);
        return value;
    }
}
