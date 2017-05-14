package cn.plugin;

import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.plugin.Task.LoginOutTimeTask;
import cn.plugin.command.unregister;
import cn.plugin.event.EventListener;


import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Handsomezixuan on 2017/5/5.
 */
public class Main extends PluginBase implements LoginAPI {
    //文件分隔符
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    //时间
    public static final Long TWODAY = 2L * 60 * 60 * 1000;
    //插件目录
    public File PluginDirectory;
    //玩家数据目录
    public File PlayerDirectory;
    //接口对象
    public static Main Instance;
    //没有登录的玩家列表
    private ArrayList<String> UnLogin = new ArrayList<String>();
    //配置文件
    Config config;
    //插件开启时
    @Override
    public void onEnable() {
        saveResource("Config.yml");
       config = new Config(getDataFolder()+FILE_SEPARATOR+"Config.yml",Config.YAML);
        PluginDirectory = new File(getDataFolder()+"");
        if(!PluginDirectory.exists())
            PluginDirectory.mkdirs();
        PlayerDirectory = new File(PluginDirectory+FILE_SEPARATOR+"Players");
        if(!PlayerDirectory.exists())
           PlayerDirectory.mkdirs();
        Instance = this;
        getServer().getPluginManager().registerEvents(new EventListener(this),this);
        getServer().getCommandMap().register("unregister",new unregister(this));
       getServer().getScheduler().scheduleRepeatingTask(new LoginOutTimeTask(this),new Integer((String) getConfig().get("LoginOutTime")) * 20);
    }

    @Override
    public boolean isLogin(Player player) {

        return isLogin(player.getName());

    }

    @Override
    public boolean isLogin(String player) {
       return !UnLogin.contains(player);
    }

    @Override
    public boolean isregister(Player player) {
        return isregister(player.getName());
    }

    @Override
    public boolean isregister(String player) {
       if(Configexist(player))return true;
        return false;
    }

    @Override
    public boolean isCreateChild(Player player) {
        String cid = player.getUniqueId().toString();
        int count=0;
        count=isChildAll(player,count,PlayerDirectory);
        int ConfigChild = (int)getConfig().get("Child");
        if(count>ConfigChild)
        return false;
        return true;
    }

    public int isChildAll(Player player,int count,File directory){
        File[] files = directory.listFiles();
        for(File file:files){
            if(file.isDirectory()){
                count=isChildAll(player,count,file);
            }else{
                String[] name=file.getName().split("\\.");
                Config PlayerFile =getPlayerInfo(name[0]);
                String ip = (String) PlayerFile.get("last-IP");
                String cid = (String) PlayerFile.get("last-cid");
                String joinCid = player.getUniqueId().toString();
                if(ip.equals((player.getAddress().toString())) || joinCid.equals(cid) ){
                    ++count;
                }
            }
        }
        return count;
    }

    public File Createpath(String name){
        char index = name.charAt(0);
        File DataDirectory= new File(PlayerDirectory+FILE_SEPARATOR+index);
        if(!DataDirectory.exists()) DataDirectory.mkdirs();
        return DataDirectory;

    }



    public void addUnlogin(Player player){
        addUnlogin(player.getName());
    }

    public void addUnlogin(String player){
        if(!UnLogin.contains(player))
            UnLogin.add(player);
    }

    public void removeUnlogin(Player player){
        removeUnlogin(player.getName());
    }

    public void removeUnlogin(String player){
            if(UnLogin.contains(player))
                UnLogin.remove(player);
    }

    public void register(Player player,String passworld){
        if(Configexist(player.getName()))
            return;
        File DataDirectory=Createpath(player.getName());
        Config PlayerConfig = new Config(DataDirectory+FILE_SEPARATOR+player.getName()+".yml",Config.YAML);
        PlayerConfig.set("last-time",genowtTime());
        PlayerConfig.set("passworld",passworld);
        PlayerConfig.set("last-cid",player.getUniqueId().toString());
        PlayerConfig.set("last-IP",player.getAddress());
        PlayerConfig.save();
    }
    public void register(String player,String passworld){
        Player p=getServer().getPlayer(player);
        register(p,passworld);
    }
    public boolean Configexist(String name){
        File DataDirectory=Createpath(name);
        for(File file:DataDirectory.listFiles()){
          String[] filename = file.getName().split("\\.");
            if(name.equals(filename[0])){
                return true;
            }
        }
        return false;
    }

    public long genowtTime(){
        return new Date().getTime();
    }

    public void updatePlayerInfo(Player player){
        File DataDirectory=Createpath(player.getName());
        Config PlayerConfig = new Config(DataDirectory+FILE_SEPARATOR+player.getName()+".yml",Config.YAML);
        PlayerConfig.set("last-time",genowtTime());
        PlayerConfig.set("last-cid",player.getUniqueId().toString());
        PlayerConfig.set("last-IP",player.getAddress().toString());
        PlayerConfig.save();
    }

    public Config getPlayerInfo(Player player){
        File DataDirectory=Createpath(player.getName());
        return new Config(DataDirectory+FILE_SEPARATOR+player.getName()+".yml",Config.YAML);
    }

    public Config getPlayerInfo(String player){
        File DataDirectory=Createpath(player);
        return new Config(DataDirectory+FILE_SEPARATOR+player+".yml",Config.YAML);
    }


    public boolean needLogin(Player p){
        File DataDirectory=Createpath(p.getName());
        Config PlayerFile = new Config(DataDirectory+FILE_SEPARATOR+p.getName()+".yml",Config.YAML);
        long lasttime = (long) PlayerFile.get("last-time");
        long now = genowtTime();
        String ip = (String) PlayerFile.get("last-IP");
        if((now - lasttime <= TWODAY) && (ip.equals(p.getAddress().toString())))
            return false;
        return true;
    }

    public boolean isLogined(Player player){
        return isLogined(player.getName());
    }

    public boolean isLogined(String player){
        return !UnLogin.contains(player);
    }

    public boolean Login(Player player,String passworld){
        Config PlayerFile = getPlayerInfo(player);
        String DataPssworld = (String)PlayerFile.get("passworld");
        if(DataPssworld.equals(passworld))
            return true;
        return false;
    }

    public Config getPluginConfig(){
        return config;
    }

}
