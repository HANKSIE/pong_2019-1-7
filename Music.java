package sample;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class Music {

    private String name;
    AudioInputStream audioInputStream;
    Clip clip;

    public Music(String n){
       this.name = n;
    }

    public void playSound() {
        try {
            audioInputStream = AudioSystem.getAudioInputStream(new File(name).getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch(Exception ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
    }

    public void stop(){
        try{
            clip.stop();
        }
        catch (NullPointerException e){
            System.out.println("nullpointer");
        }catch (Exception e2){
            System.out.println("unknowed error");
        }

    }

    public void playAndLoop(){
        try {
            audioInputStream = AudioSystem.getAudioInputStream(new File(name).getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch(Exception ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
    }

}
