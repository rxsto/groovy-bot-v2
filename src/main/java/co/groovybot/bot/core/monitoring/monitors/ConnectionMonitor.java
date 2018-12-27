package co.groovybot.bot.core.monitoring.monitors;

import co.groovybot.bot.core.monitoring.Monitor;
import fr.bmartel.speedtest.SpeedTestSocket;
import org.influxdb.dto.Point;

public class ConnectionMonitor extends Monitor {

    @Override
    public Point save() {
        SpeedTestSocket speedTestSocket = new SpeedTestSocket();

        speedTestSocket.startDownload("http://speedcheck-ham.kabeldeutschland.de/speedtest/upload.php");

        speedTestSocket.getLiveReport();

        return Point.measurement("connection_info")
                .addField("connection_download", 2).build();
    }
}
