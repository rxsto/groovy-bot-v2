package co.groovybot.bot.core.monitoring.monitors.internet;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;
import fr.bmartel.speedtest.model.SpeedTestMode;
import io.prometheus.client.Gauge;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Log4j2
public class InternetMonitor implements Runnable {

    private final SpeedTestSocket speedTestSocket;

    private final Gauge httpDownload = Gauge.build().namespace("groovy").name("http_download").help("Download speed over http in bit / s").register();
    private final Gauge httpUpload = Gauge.build().namespace("groovy").name("http_upload").help("Upload speed over http in bit / s").register();

    private boolean downloadRunning = false;
    private boolean uploadRunning = false;

    private final ScheduledFuture<?> future;

    public InternetMonitor() {
        speedTestSocket = new SpeedTestSocket();
        speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {
            @Override
            public void onCompletion(SpeedTestReport report) {
                if (report.getSpeedTestMode() == SpeedTestMode.UPLOAD) {
                    httpUpload.set(report.getTransferRateBit().doubleValue());
                    uploadRunning = false;
                } else if (report.getSpeedTestMode() == SpeedTestMode.DOWNLOAD) {
                    httpDownload.set(report.getTransferRateBit().doubleValue());
                    downloadRunning = false;
                }
            }

            @Override
            public void onProgress(float percent, SpeedTestReport report) {
                // We don't need that
                if (report.getSpeedTestMode() == SpeedTestMode.UPLOAD) {
                    uploadRunning = true;
                } else if (report.getSpeedTestMode() == SpeedTestMode.DOWNLOAD) {
                    downloadRunning = false;
                }
            }

            @Override
            public void onError(SpeedTestError speedTestError, String errorMessage) {
                log.error("[InternetMonitor] Error {} while testing speed with message {}", speedTestError.name(), errorMessage);
            }
        });
        future = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this, 0, 30, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        if (!downloadRunning) {
            speedTestSocket.startDownload("http://ipv4.ikoula.testdebit.info/1M.iso");
            downloadRunning = true;
        }

        /*if (!uploadRunning) {
            speedTestSocket.startUpload("http://ipv4.ikoula.testdebit.info/", 1000000);
            uploadRunning = true;
        }*/
    }
}
