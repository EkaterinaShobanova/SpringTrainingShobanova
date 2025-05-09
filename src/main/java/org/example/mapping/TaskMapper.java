package org.example.mapping;

import org.example.dto.TaskDto;
import org.example.entity.Task;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {

    @Mapping(source = "taskId", target = "id")
    Task fromDto(TaskDto dto);

    @Mapping(source = "id", target = "taskId")
    TaskDto toDto(Task task);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(TaskDto dto, @MappingTarget Task entity);
}
