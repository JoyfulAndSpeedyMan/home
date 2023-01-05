package top.pin90.home.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
@Slf4j
public class TestController {

    private AtomicLong count = new AtomicLong(0);

    @GetMapping("/t")
    public String t() {
        Thread thread = Thread.currentThread();
        log.info("呵呵哈哈哈 {}", thread);
        return thread.toString();
    }

    @GetMapping("/currentTime")
    public String currentTime() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        long time = System.currentTimeMillis();
//        Thread thread = Thread.currentThread();
//        List<Thread> vtList = null;
//        try {
//            Field containerField = Thread.class.getDeclaredField("container");
//            Field carrierThreadField = thread.getClass().getDeclaredField("carrierThread");
//            containerField.setAccessible(true);
//            carrierThreadField.setAccessible(true);
//            ThreadContainer container = (ThreadContainer) containerField.get(thread);
//            Thread carrierThread = (Thread) carrierThreadField.get(thread);
//            Method threadStateMethod = Thread.class.getDeclaredMethod("threadState");
//            threadStateMethod.setAccessible(true);
////            Thread.State invoke = (Thread.State) threadStateMethod.invoke(carrierThread);
//            vtList = container.threads().collect(Collectors.toList());
//
//            HashMap<Thread.State, Integer> count = new HashMap<>();
//
//            for (Thread vt : vtList) {
//                Thread.State state = (Thread.State) threadStateMethod.invoke(vt);
//                count.compute(state, (k, v) -> v == null ? 0 : v + 1);
//            }
//            log.info("currentTime time {} count {} --- t {}  ------ vtList {}", time, count, thread, vtList);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        log.info("currentTime time {} count {} t {}", time, count.getAndIncrement(), thread);
        return time + "";
    }
}
