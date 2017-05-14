package cn.plugin;

import cn.nukkit.Player;

import java.util.UUID;

/**
 * Created by Handsomezixuan on 2017/5/5.
 */
public interface LoginAPI {
    /**
     *
     */
    static final Main getInstance=Main.Instance;
    boolean isLogin(Player player);

    boolean isLogin(String player);

    boolean isregister(Player player);

    boolean isregister(String player);

    boolean isCreateChild(Player player);


}
