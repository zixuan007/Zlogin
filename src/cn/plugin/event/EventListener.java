package cn.plugin.event;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.utils.Config;
import cn.plugin.LoginAPI;
import cn.plugin.Main;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Handsomezixuan on 2017/5/5.
 */
public class EventListener implements Listener {
    //主类对象
    Main main;
    //正在准备注册的玩家
    HashMap<String,String> registering = new HashMap<String,String>();
    public EventListener(Main main){
        this.main = main;
    }


    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent e){
        Player p = e.getPlayer();
        if(!main.isLogin(p))
        e.setCancelled(true);
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent e){
        Player p = e.getPlayer();
        if(!main.isregister(e.getPlayer())){
            e.getPlayer().sendTitle((String)main.getConfig().get("unregister-message"));
            e.setCancelled(true);
            return;
        }
        if(!main.isLogin(p)) {
            e.getPlayer().sendTitle((String) main.getConfig().get("unlogin-message"));
            e.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        if((boolean)main.getConfig().get("spawn")){
           p.teleport(p.getLevel().getSafeSpawn());
        }
       if(main.isregister(p)){
           if((boolean) main.getConfig().get("Lock-Cid")){
               Config PlayerFile=main.getPlayerInfo(p);
               String cid = (String)PlayerFile.get("last-cid");
               String PlayerJoinCid = p.getUniqueId().toString();
               if(cid.equals(PlayerJoinCid)){
                   p.sendMessage((String)main.getConfig().get("alikecid-login-message"));
                   return;
               }
           }
           if(!main.needLogin(p)){
               p.sendMessage((String)main.getConfig().get("alikeIP-login-message"));
           }else{
               main.addUnlogin(p);
               p.sendMessage((String) main.getConfig().get("unlogin-message"));
           }
       }else{
           if(!main.isCreateChild(p)){
               p.kick((String) main.getConfig().get("child-message"),false);
               return;
           }
           main.addUnlogin(p);
           p.sendMessage((String)main.getConfig().get("first-join-message"));
       }
    }


    @EventHandler(priority=EventPriority.HIGHEST)
    public void onchat(PlayerChatEvent event){
        Player player = event.getPlayer();
        if(main.isLogined(player))
            return;
        event.setCancelled(true);
        if(!main.isregister(player)){
            if(!registering.containsKey(player.getName())){
                if(!(event.getMessage().equals(player.getName()))){
                    player.sendMessage("ID 验证失败, 请正确输入你的 ID");
                    return;
                }else{
                    registering.put(player.getName(),null);
                    player.sendMessage("ID 验证成功, 请输入你想要注册的密码");
                    return;
                }
            }else if(registering.get(player.getName()) == null){
                if(event.getMessage().length() <6){
                   player.sendMessage("密码长度必须大于等于6请重新输入");
                   return;
                }
                if(player.getName().equals(event.getMessage())){
                    player.sendMessage("密码不能和ID名字相同");
                    return;
                }
                String passworld = event.getMessage();
                if(passworld.equals("123456") ||
                   passworld.equals("654321") ||
                   passworld.equals("012345") ||
                   passworld.equals("abcdefg")||
                   passworld.equals(" ")){
                    player.sendMessage("密码过于简单,请重新输入密码");
                    return;
                }
                registering.put(player.getName(),passworld);
                player.sendMessage("你设置的密码是:"+passworld);
                player.sendMessage("请再次输入一次密码");
                return;

            }else if(registering.get(player.getName()) != null){
                String passworld = event.getMessage();
                if(registering.get(player.getName()).equals(passworld)){
                        main.register(player,passworld);
                        player.sendMessage((String) main.getConfig().get("register-message"));
                        registering.remove(player.getName());
                        main.removeUnlogin(player);
                        return;
                }else{
                    player.sendMessage("两次输入密码不同. 请重新设置密码");
                    registering.put(player.getName(),null);
                    return;
                }
            }
        }else{
                if(main.Login(player,event.getMessage())){
                    main.updatePlayerInfo(player);
                    main.removeUnlogin(player);
                    player.sendMessage((String)main.getConfig().get("login-message"));
                }else{
                   player.sendMessage("登录失败, 密码错误");
                }
        }

    }
    @EventHandler(priority=EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event){
        if(!main.isLogin(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event){
        if(!main.isLogin(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event){
        if(!main.isLogin(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event){
        if(!main.isregister(event.getPlayer())){
            if(registering.containsKey(event.getPlayer().getName())){
                registering.remove(event.getPlayer().getName());
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event){
        if(!main.isLogin(event.getEntity().getName())){
            event.setCancelled(true);
        }
        if(event instanceof EntityDamageByEntityEvent){
            if(!main.isLogin(((EntityDamageByEntityEvent) event).getDamager().getName())){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onDropItem(PlayerDropItemEvent event){
        if(!main.isLogin(event.getPlayer().getName())){
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onItemHeld(PlayerItemHeldEvent event){
        if(!main.isLogin(event.getPlayer().getName())){
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onTeleport(PlayerTeleportEvent event){
        if(!main.isLogin(event.getPlayer().getName()) && !((boolean)main.getConfig().get("spawn"))){
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onRegainHealth(EntityRegainHealthEvent event){
        if(!main.isLogin(event.getEntity().getName())){
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPreLogin(PlayerPreLoginEvent event){
        Player p = event.getPlayer();
        if((p.getName().length())>((int)main.getConfig().get("name-length"))){
            p.kick((String) main.getConfig().get("name-length-message"),false);
            return;
        }
        main.getServer().getOnlinePlayers().forEach((uuid,player)->{
            String name = player.getName();
            String loginname = p.getName();
            if(loginname.equals(name)){
                p.kick((String) main.getConfig().get("Crowded"),false);
                return;
            }

        });
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPick(InventoryPickupItemEvent event){
//        main.getServer().getOnlinePlayers().forEach((uuid,player)->{
//            if(!main.isLogin(player)){
//               event.getInventory().remove(event.getItem().getItem());
//
//            }else{
//                event.setCancelled(false);
//            }
//        });
        for(Player p:event.getViewers()){
            main.getServer().getLogger().info(p.getName());
        }
    }


}
