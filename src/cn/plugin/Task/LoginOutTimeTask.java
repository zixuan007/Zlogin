package cn.plugin.Task;

import cn.nukkit.scheduler.PluginTask;
import cn.plugin.Main;

/**
 * Created by Handsomezixuan on 2017/5/9.
 */
public class LoginOutTimeTask extends PluginTask<Main> {
    Main main;
    public LoginOutTimeTask(Main main) {
        super(main);
        this.main = main;
    }

    @Override
    public void onRun(int i) {
        if(!(main.getServer().getOnlinePlayers().size() > 0))return;
       main.getServer().getOnlinePlayers().forEach(((uuid, player) -> {
           if(!main.isregister(player) || !main.isLogin(player)){
               player.kick("[Zlogin]:由于您长时间不登录账号或者是没注册账号,登录超时",false);
           }
       }));
    }
}
