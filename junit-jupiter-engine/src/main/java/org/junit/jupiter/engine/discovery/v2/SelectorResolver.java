package org.junit.jupiter.engine.discovery.v2;

import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

interface SelectorResolver {
    Set<Class<? extends DiscoverySelector>> getSupportedSelectorTypes();
    Optional<Result> resolveSelector(DiscoverySelector selector, Context context);
    Optional<Result> resolveUniqueId(UniqueId uniqueId, Context context);

    class Result {
        private final TestDescriptor testDescriptor;
        private final Supplier<Set<? extends DiscoverySelector>> additionalSelectorsSupplier;
        private boolean perfectMatch = true;

        static Result of(TestDescriptor testDescriptor) {
            return new Result(testDescriptor, Collections::emptySet);
        }

        static Result of(Supplier<Set<? extends DiscoverySelector>> additionalSelectorsSupplier) {
            return new Result(null, additionalSelectorsSupplier);
        }

        static Result of(TestDescriptor testDescriptor, Supplier<Set<? extends DiscoverySelector>> additionalSelectorsSupplier) {
            return new Result(testDescriptor, additionalSelectorsSupplier);
        }

        private Result(TestDescriptor testDescriptor, Supplier<Set<? extends DiscoverySelector>> additionalSelectorsSupplier) {
            this.testDescriptor = testDescriptor;
            this.additionalSelectorsSupplier = additionalSelectorsSupplier;
        }

        public boolean isPerfectMatch() {
            return perfectMatch;
        }

        public Result withPerfectMatch() {
            return withPerfectMatch(true);
        }

        public Result withPerfectMatch(boolean perfectMatch) {
            if (this.perfectMatch == perfectMatch) {
                return this;
            }
            Result result = new Result(testDescriptor, additionalSelectorsSupplier);
            result.perfectMatch = perfectMatch;
            return result;
        }

        Optional<TestDescriptor> getTestDescriptor() {
            return Optional.ofNullable(testDescriptor);
        }

        Set<? extends DiscoverySelector> getAdditionalSelectors() {
            return additionalSelectorsSupplier.get();
        }
    }

    interface Context {
        <T extends TestDescriptor> Optional<T> addToEngine(Function<TestDescriptor, Optional<T>> creator);
        <T extends TestDescriptor> Optional<T> addToParentWithSelector(DiscoverySelector selector, Function<TestDescriptor, Optional<T>> creator);
    }

}
