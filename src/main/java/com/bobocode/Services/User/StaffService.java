package com.bobocode.Services.User;

import com.bobocode.Entities.Users.AbstractUser;
import com.bobocode.Entities.Users.Staff;
import com.bobocode.Exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class StaffService{

    private final Map<Long, AbstractUser> allUsers;

    public void addNewStaff(Staff newStaff){
        newStaff.setId(AbstractUser.globalIdCounter++);
        allUsers.put(newStaff.getId(), newStaff);
    }

    public void editStaff(long id, Staff editedStaff){
        if (allUsers.containsKey(id)) {
            allUsers.put(id, editedStaff);
        }
    }

    public void removeStaff(long id){
        AbstractUser user = allUsers.get(id);

        if (user == null) {
            throw new EntityNotFoundException("Staff with ID " + id + " not found!");
        }

        if (!(user instanceof Staff)) {
            throw new IllegalArgumentException("User with this ID is not a Staff member!");
        }

        allUsers.remove(id);
    }

    public List<Staff> getAllStaff() {
        return allUsers.values().stream()
                .filter(Staff.class::isInstance)
                .map(Staff.class::cast)
                .toList();
    }

    public Staff getStaffById(long id) {
        AbstractUser user = allUsers.get(id);

        if (user instanceof Staff staff) {
            return staff;
        }

        throw new EntityNotFoundException("HTTP STATUS 404 : Staff with ID " + id + " not found!");
    }
}
