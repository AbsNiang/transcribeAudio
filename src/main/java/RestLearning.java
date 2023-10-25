import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RestLearning {

    public static void main(String[] args) {
        try {
            Transcript transcript = new Transcript();
            transcript.setAudio_url("https://github.com/AbsNiang/transcribeAudioFile/blob/master/Thirsty.mp4?raw=true");
            Gson gson = new Gson(); //allows us to change the transcript object into json
            String jsonRequest = gson.toJson(transcript);
            //POST
            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(new URI("https://api.assemblyai.com/v2/transcript"))
                    .header("Authorization", Constants.APIkey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();

            HttpClient httpClient = HttpClient.newHttpClient();
            //json response including id, status etc...
            HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println(postResponse.body());
            //GET
            transcript = gson.fromJson(postResponse.body(), Transcript.class);
            System.out.println(transcript.getId());
            HttpRequest getRequest = HttpRequest.newBuilder() //GET is default, could add .GET(HttpRequest...) after header if we wanted but not necessary
                    .uri(new URI("https://api.assemblyai.com/v2/transcript/" + transcript.getId()))
                    .header("Authorization", Constants.APIkey)
                    .build();

            while (true) {
                HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
                transcript = gson.fromJson(getResponse.body(), Transcript.class);

                System.out.println(transcript.getStatus());
                if (transcript.getStatus().equals("completed") || transcript.getStatus().equals("error")) {
                    break;
                }
                Thread.sleep(1000); //waits 1s before continuing
            }
            System.out.println("Transcription completed.");
            System.out.println("Transcript is: " + transcript.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
