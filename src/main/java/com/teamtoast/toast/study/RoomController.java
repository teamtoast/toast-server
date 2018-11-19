package com.teamtoast.toast.study;

import com.teamtoast.toast.SocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoomController {

    @Autowired
    private SocketHandler socketHandler;

    @GetMapping("/studyrooms")
    public Room.Info[] getRooms() {
        return socketHandler.getRoomInfos();
    }

    @GetMapping("/studyrooms/{categoryId}")
    public Room.Info[] getRooms(@PathVariable int categoryId) {
        return socketHandler.getRoomInfos();
    }

}
