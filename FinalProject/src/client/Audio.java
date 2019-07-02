package client;


import java.util.Base64;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Audio {
    public Audio()
    {
        try {
            AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 8000, 16, 2, 4, 8000, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported.");
            }

            final TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(info);
            targetLine.open();
            System.out.println("Starting recording");
            targetLine.start();

            Thread thread = new Thread()
            {
                @Override
                public void run()
                {
                    AudioInputStream audioStream = new AudioInputStream(targetLine);
                    File audioFile = new File("record.wav");
                    try {
                        AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, audioFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Stopped recording");
                }
            };

            thread.start();
            Thread.sleep(2000);
            targetLine.stop();
            targetLine.close();

        } catch (LineUnavailableException e) {
                e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getAudioData()
    {
        try {
            File f = new File("record.wav");
            byte[] sound = Files.readAllBytes(f.toPath());
            byte[] encodedSound = Base64.getEncoder().encode(sound);
            return new String(encodedSound);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
