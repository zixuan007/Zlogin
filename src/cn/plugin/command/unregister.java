package cn.plugin.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.Config;
import cn.plugin.Main;

/**
 * Created by Handsomezixuan on 2017/5/9.
 */
public class unregister extends Command {
    Main main;
    public unregister(Main main){
        super("unregister","修改登录密码","/unregister");
        this.main = main;
        getCommandParameters().put("default",new CommandParameter[]{
                new CommandParameter("原密码",CommandParameter.ARG_TYPE_STRING,true),
                new CommandParameter("新密码",CommandParameter.ARG_TYPE_STRING,true)
        });
    }
    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if("unregister".equals(getName().toLowerCase())){
            Config PlayerConfig=main.getPlayerInfo((Player) sender);
            String passworld = (String)PlayerConfig.get("passworld");
            if(args.length<1){
                sender.sendMessage("/unregister [原密码] [新密码] >>修改密码");
                return true;
            }
            if(!args[0].equals(passworld)){
                sender.sendMessage("原密码不正确");
                return true;
            }
            if(args[1].equals(passworld)){
                sender.sendMessage("原密码不能和新密码一样");
                return true;
            }
            if(args[1].equals("123456") ||
               args[1].equals("012345") ||
               args[1].equals(sender.getName()) ||
               args[1].equals("abcdefg") ||
               args[1].equals("fedcba")){
                sender.sendMessage("密码过于简单");
                return true;
            }
            PlayerConfig.set("passworld",args[1]);
            PlayerConfig.save();
            sender.sendMessage("修改密码成功");
            main.addUnlogin((Player)sender);
            return true;
        }
        return false;
    }
}
