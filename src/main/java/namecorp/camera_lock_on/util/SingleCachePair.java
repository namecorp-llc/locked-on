package namecorp.camera_lock_on.util;
import java.util.function.Function;

/*
 * A simple cache that holds only one value per time.
 * It has a two level key to combine two keys together
 * @param <TOuterKey> The type of the outer key to be cached.
 * @param <TInnerKey> The type of the inner key to be cached.
 * @param <TValue> The type of the value to be cached.
 */
public class SingleCachePair<TOuterKey, TInnerKey, TValue> {
    private SingleCache<TOuterKey, SingleCache<TInnerKey, TValue>> cache = new SingleCache<>();
    public TValue get(TOuterKey outterKey, TInnerKey innerKey, Function<TInnerKey, TValue> valueSupplier) {
        return cache.get(outterKey, x -> new SingleCache<TInnerKey, TValue>())
            .get(innerKey, valueSupplier);
    }
}
