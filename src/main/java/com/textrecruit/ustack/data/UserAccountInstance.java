package com.textrecruit.ustack.data;

public class UserAccountInstance {

    private static UserAccount userAccount = UserAccount.getInstance();

    /**
     * Get a user account by uid
     *
     * @param uid
     * @return
     */
    public UserAccount getUserByIdWrapper(String uid) {
        return userAccount.getUserById(uid);
    }
    /**
     * Get a user account by name
     *
     * @param userName
     * @return
     */
    public UserAccount getUser(String userName)
    {
        return userAccount.getUser(userName);
    }
}
