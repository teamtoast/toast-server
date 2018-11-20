package com.teamtoast.toast.study;

import com.teamtoast.toast.category.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;

public interface RoomRepository extends CrudRepository<Room.Data, Long> {

    @Query(value = "UPDATE studyrooms SET ended_at=?2 WHERE id=?1", nativeQuery = true)
    void setEndedAt(long id, Date date);

}
