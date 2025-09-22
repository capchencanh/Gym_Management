package com.dhd.gymmanagement.service;

import com.dhd.gymmanagement.entity.WorkoutLogComment;
import java.util.List;

public interface WorkoutLogCommentService {


    WorkoutLogComment addComment(Integer logId, Integer ptId, String comment);


    List<WorkoutLogComment> getCommentsByLogId(Integer logId);
}
