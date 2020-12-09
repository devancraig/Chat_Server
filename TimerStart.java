import java.io.Serializable;
import java.util.*;


public class TimerStart extends Object implements Serializable{

    public Timer myTimer(Timer timer) {
        timer.cancel();
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                System.exit(0);
                System.out.println("Server Ending");
            }
        };

        timer = new Timer();
        timer.schedule(timerTask, 300000);
        return timer;
    }

}