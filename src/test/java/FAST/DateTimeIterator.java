package FAST;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.function.Supplier;

public class DateTimeIterator implements Iterator<LocalDateTime> {
    private LocalDateTime currentDateTime;
    private Supplier<LocalDateTime> incrementFunction;

    public DateTimeIterator(LocalDateTime startDateTime, Supplier<LocalDateTime> incrementFunction) {
        this.currentDateTime = startDateTime;
        this.incrementFunction = incrementFunction;
    }

    @Override
    public boolean hasNext() {
        return true; // Ajustez la condition selon vos besoins
    }

    @Override
    public LocalDateTime next() {
        LocalDateTime value = currentDateTime;
        currentDateTime = incrementFunction.get();
        return value;
    }
}

