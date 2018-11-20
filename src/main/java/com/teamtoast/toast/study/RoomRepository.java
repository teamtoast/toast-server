package com.teamtoast.toast.study;

import com.teamtoast.toast.category.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface RoomRepository extends CrudRepository<Room.Data, Long> {

}
