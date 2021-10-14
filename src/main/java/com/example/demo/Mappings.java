package com.example.demo;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.WriteResult;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Configuration
@SpringBootApplication
@Controller
public class Mappings{
    @GetMapping("/")
    public String messageSource(Model model){
        NewsInterface.loadInformation();
        String json = NewsInterface.contents.toString();
        String[] jarr = json.split("\\{");
        StringBuilder build = new StringBuilder();
        String[] jarr2 = new String[2];
        for(int i = 0; i < jarr.length;i++) {
            if(jarr[i].contains("}")){
                jarr2 = jarr[i].split("}");
                if(jarr2.length>0) {
                    jarr2[0] = jarr2[0].replaceAll("\"", "\\\\\"");
                    if (jarr2.length == 2) {
                        build.append("\"{" + jarr2[0] + "}\"" + jarr2[1]);
                    } else {
                        build.append("\"{" + jarr2[0] + "}\"");
                    }
                }
            }
            else {
                build.append(jarr[i]);
            }
        }
        model.addAttribute("database",NewsInterface.contents.values());
        model.addAttribute("delete","false");
        return "content-list.html";
    }
    @PostMapping("/")
    public String changeList(@RequestParam("ID") String id, @RequestParam("Title") String title, @RequestParam("Description") String description, @RequestParam("Image") String image, @RequestParam(value = "Video",required = false) String video, Model model) throws InterruptedException {
        NewsInterface.loadInformation();

        if(id!=null){
            Map<String, Object> docData = new HashMap<>();
            docData.put("id", id);
            docData.put("title", title);
            docData.put("description", description);
            docData.put("image", image);
            docData.put("video", video);
            ApiFuture<WriteResult> future = NewsInterface.db.collection("test-news").document(id).set(docData);
        }
        String json = NewsInterface.contents.toString();
        String[] jarr = json.split("\\{");
        StringBuilder build = new StringBuilder();
        String[] jarr2 = new String[2];
        for(int i = 0; i < jarr.length;i++) {
            if(jarr[i].contains("}")){
                jarr2 = jarr[i].split("}");
                if(jarr2.length>0) {
                    jarr2[0] = jarr2[0].replaceAll("\"", "\\\\\"");
                    if (jarr2.length == 2) {
                        build.append("\"{" + jarr2[0] + "}\"" + jarr2[1]);
                    } else {
                        build.append("\"{" + jarr2[0] + "}\"");
                    }
                }
            }
            else {
                build.append(jarr[i]);
            }
        }
        model.addAttribute("database",json);
        model.addAttribute("id",id);
        TimeUnit.MILLISECONDS.sleep(300);
        return "redirect:/";
    }
    @RequestMapping("/alter")
    String alter(@RequestParam(value = "ID",required = false) String id,@RequestParam(value = "Title",required = false) String title,@RequestParam(value = "Description",required = false) String description,@RequestParam(value = "Image",required = false) String image,@RequestParam(value = "Video",required = false) String video,@RequestParam(value = "delete",required = false) String delete, Model model) {
        StringBuilder build = new StringBuilder();
        if(id!="" && id!=null) {
            if (delete == null) {
                System.out.println(id.toString());
                Map<String, Object> docData = new HashMap<>();
                docData.put("id", id);
                docData.put("title", title);
                docData.put("description", description);
                docData.put("image", image);
                docData.put("video", video);
                ApiFuture<WriteResult> future = NewsInterface.db.collection("test-news").document(id).set(docData);
            } else {
                NewsInterface.db.collection("test-news").document(id).delete();
            }
            return "redirect:/alter";
        }
        String json = NewsInterface.contents.toString();
        model.addAttribute("id",id);

        return "content-alter.html";
    }
    @RequestMapping(value = "/{ID}", method = RequestMethod.GET)
    String getDynamicUriValue(@PathVariable String ID,@RequestParam(value = "delete",required = false) String delete, Model model) throws ExecutionException, InterruptedException {
        if(delete != null && delete!=""){
            NewsInterface.db.collection("test-news").document(ID).delete();
            TimeUnit.MILLISECONDS.sleep(300);
            return "redirect:/";
        }
        DocumentReference docRef = NewsInterface.db.collection("test-news").document(ID);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        String id = document.getString("id");
        String Title = document.getString("title");
        String Description = document.getString("description");
        String Image = document.getString("image");
        String Video = document.getString("video");
        model.addAttribute("id",id);
        model.addAttribute("title",Title);
        model.addAttribute("description",Description);
        model.addAttribute("image",Image);
        model.addAttribute("video",Video);
        return "content-modify.html";
    }
}
