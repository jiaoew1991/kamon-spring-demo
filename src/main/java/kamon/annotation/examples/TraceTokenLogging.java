package kamon.annotation.examples;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import kamon.Kamon;
import kamon.trace.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TraceTokenLogging {

    public void testTraceToken() throws InterruptedException {
        Kamon.start();

        final ActorSystem system = ActorSystem.create("trace-token-logging");
        final ActorRef upperCaser = system.actorOf(Props.create(UpperCaser.class), "upper-caser");

        LoggingAdapter logger = Logging.getLogger(system, this);
        // Send five messages without a TraceContext
        for (int i = 0; i < 5; i++) {
            upperCaser.tell("Hello without context", ActorRef.noSender());
        }

        // Wait a bit to avoid spaghetti logs.
        Thread.sleep(1000);


        // tag:sending-async-events:start
        // Send five messages with a TraceContext
        for (int i = 0; i < 5; i++) {
            Tracer.withNewContext("simple-test", () -> {
                logger.info("askdjfakjdflasdjk");
                upperCaser.tell("Hello with simple context", ActorRef.noSender());
                return null;
            });
        }
        // tag:sending-async-events:end


        // Wait a bit for everything to be logged and shutdown.
        Thread.sleep(2000);
        system.shutdown();
    }

    public static void main(String[] args) throws InterruptedException {
        new TraceTokenLogging().testTraceToken();
    }


    // tag:trace-token-logging:start
    public static class UpperCaser extends UntypedActor {
        private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
        private final ActorRef lengthCalculator = context().actorOf(Props.create(LengthCalculator.class), "length-calculator");

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof String) {
                log.info("Upper casing [{}]", message);
                lengthCalculator.forward(((String) message).toUpperCase(), getContext());
            }
        }
    }

    public static class LengthCalculator extends UntypedActor {
        private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

        @Override
        public void onReceive(Object message) throws Exception {
            log.debug("message is {}", message);
            if (message instanceof String) {
                log.info("Calculating the length of: [{}]", message);
            }
        }
    }
// tag:trace-token-logging:end
}
