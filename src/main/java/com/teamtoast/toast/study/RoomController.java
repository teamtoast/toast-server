package com.teamtoast.toast.study;

import com.teamtoast.toast.SocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;

@RestController
public class RoomController {

    @Autowired
    private RoomRepository repository;

    @Autowired
    private SocketHandler socketHandler;

    @Value("${speech-data-path}")
    private String speechPath;

    @GetMapping("/studyrooms")
    public Room.Info[] getRooms() {
        return socketHandler.getRoomInfos();
    }

    @GetMapping("/studyrooms/{categoryId}")
    public Room.Info[] getRooms(@PathVariable int categoryId) {
        return socketHandler.getRoomInfos();
    }

    @GetMapping("/studyrooms/feedbacks/{userId}")
    public Room.Data[] getFeedbackRooms(@PathVariable long userId) {
        ArrayList<Room.Data> list = new ArrayList<>();
        for(Room.Data data : repository.findAll()) {
            if(new File(speechPath + "/" + data.getId() + "/" + userId).exists()) {
                list.add(data);
            }
        }

        return list.toArray(new Room.Data[0]);
    }

}
