package kirya.view.events;

import javafx.event.Event;
import javafx.event.EventType;
import kirya.view.enums.Page;

public class PageRequestEvent extends Event {
    /**
     * Event to request a page.
     */
    public static final EventType<PageRequestEvent> PAGE_REQUEST = new EventType<>(Event.ANY, "PAGE_REQUEST");
    private final Page requestedPage;

    public PageRequestEvent(Page requestPage) {
        this.requestedPage = requestPage;
        super(PAGE_REQUEST);
    }

    public Page getRequestedPage() {
        return this.requestedPage;
    }
}
