package kr.dreamstory.ability.java.pixelmaker.gui;

import kr.dreamstory.ability.ability.VariableKt;
import kr.dreamstory.ability.ability.play.island.DSIsland;
import kr.dreamstory.ability.api.DSCoreAPI;
import kr.dreamstory.ability.core.main.DSAbility;
import com.dreamstory.ability.java.pixelmaker.sys.DrawIcon;
import com.dreamstory.ability.java.pixelmaker.sys.Palette;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PixelGUI {

    private static final Map<UUID, PixelGUI> guiMap;
    private static final List<Integer> drawSlot;
    private static final List<Integer> colorSlot;
    private static final String upEscape;
    private static final String leftEscape;
    private static final List<Palette> colors;
    private static final int MAX_COLOR_PAGE;
    private static final int MAX_DRAW_PAGE;
    private static int task;
    private static DSAbility main;
    private static Server server;
    private ClickMode mode;
    private int drawPage;
    private int colorPage;
    private List<DrawIcon> drawingSlot;
    private Palette currentColor;
    private final UUID uuid;

    public static void serverDown() {
        for (PixelGUI gui : guiMap.values()) {
            gui.remove();
            gui.getPlayer().closeInventory();
        }
    }

    static {
        guiMap = new HashMap<>();
        drawSlot = Arrays.asList(19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43);
        colorSlot = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        upEscape = "\uF806\uF802";
        leftEscape = "\uF801";
        colors = new ArrayList<>();
        colors.addAll(Arrays.asList(Palette.values()));
        for(int i = 0;i < 6;i++) colors.add(colors.get(i));

        MAX_COLOR_PAGE = colors.size() - 7;
        MAX_DRAW_PAGE = 4;
    }

    private int startTask() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (guiMap.size() == 0) {
                        task = 0;
                        this.cancel();
                        return;
                    }
                    for (PixelGUI gui : guiMap.values()) {
                        Player p = gui.getPlayer();
                        p.sendTitle(gui.toString() + "             " + gui.toString(), null, 0, 5, 0);
                    }
                } catch (Exception e) {
                }
            }
        }.runTaskTimerAsynchronously(main, 0L, 1L).getTaskId();
    }

    public PixelGUI(UUID uuid, String name, DSIsland island) {
        this.uuid = uuid;
        if (main == null) {
            main = VariableKt.getMain();
            server = main.getServer();
        }
        this.islandData = island;
        if(!check()) return;
        mode = ClickMode.NORMAL;
        inv = server.createInventory(null, 54, name);

        drawPage = 0;
        colorPage = 0;
        drawingSlot = new ArrayList<>();

        server.getScheduler().runTaskAsynchronously(main,() -> {
            String current = islandData.getIcon();
            if(current == null || current.equalsIgnoreCase("")) {
                for (int j = 0; j < 7; j++) {
                    for (int i = 0; i < 7; i++) {
                        drawingSlot.add(new DrawIcon(j));
                    }
                }
            } else {
                String a = current.replace(leftEscape,"").replace(upEscape,"").replace("§x","#").replace("§","");
                a = DrawIcon.replaceUniCode(a);
                for(int j = 0;j < 7;j++) {
                    for (int i = 0; i < 7; i++) {
                        drawingSlot.add(new DrawIcon(a.substring(0,7),j));
                        if(i == 6 && j == 6) break;
                        a = a.substring(7);
                    }
                }
            }
            setItem("§f", null, Material.PAPER, 0, 24);
            setItem("§f", null, Material.PAPER, 8, 5);

            setItem("§f",null, Material.PAPER, 18, 2);
            setItem("§f",null, Material.PAPER, 36, 3);
            setItem("초기화", null, Material.PAPER, 44, 6);
            setItem("브러쉬", null, ClickMode.NORMAL.getMaterial(), 15, ClickMode.NORMAL.customModel);
            setItem("스포이드", null, ClickMode.SPOID.getMaterial(), 16, ClickMode.SPOID.customModel);
            setItem("페인트", null, ClickMode.PAINT.getMaterial(), 17, ClickMode.PAINT.customModel);
            setItem("완성", null, Material.PAPER, 26, 23);
            setItem("§f",null,Material.RABBIT_HIDE,53,5);
            init();
            server.getScheduler().runTaskLater(main,()->{
                getPlayer().openInventory(inv);
                guiMap.put(uuid, this);
                if (task == 0) { task = startTask(); }
            },1L);
        });
    }

    private void init() {
        for (int i = 0; i < 7; i++) {
            int index = colorPage + i;
            int slot = 1 + i;
            Palette color = colors.get(index);
            setItem(color.getColor() + "■", null, color.getMaterial(), slot, color.getCustom_model());
            if(slot == 4) { currentColor = color; }
        }
        for (int i = 0; i < 21; i++) {
            int index = drawPage * 7 + i;
            int line = (i / 7) * 9;
            int slot = 19 + (i % 7) + line;
            DrawIcon di = drawingSlot.get(index);
            Palette palette = di.getPalette();
            setItem(palette.getColor() + "■", null, palette.getMaterial(), slot, palette.getCustom_model());
        }
        //setItem(currentColor.getColor() + "■", null, currentColor.getMaterial(), 9, currentColor.getCustom_model());
        setItem("선택 툴", Arrays.asList("§f현재 모드 : " + mode.getName(), "클릭시 브러쉬로 초기화 합니다."), mode.getMaterial(), 10, mode.getCustomModel());
    }

    private void refresh() {
        server.getScheduler().runTaskAsynchronously(main, this::init);
    }

    public void clickInv(InventoryClickEvent e) {
        e.setCancelled(true);
        if(!check()) return;
        ClickType type = e.getClick();
        int slot = e.getRawSlot();
        if(slot == 4) return;
        if (colorSlot.contains(slot)) {
            int offset = slot - 4;
            if(offset > 0) {
                for(int i = 0;i < offset;i++) {
                    if (colorPage >= MAX_COLOR_PAGE) colorPage = 0;
                    else colorPage++;
                }
            } else {
                for(int i = 0;i < offset*-1;i++) {
                    if(colorPage <= 0) colorPage = MAX_COLOR_PAGE;
                    else colorPage--;
                }
            }
            refresh();
        } else if (drawSlot.contains(slot)) {
            slot = drawPage * 7 + drawSlot.indexOf(slot);
            Palette clickColor = drawingSlot.get(slot).getPalette();
            if(currentColor.equals(clickColor)) return;
            if(mode.equals(ClickMode.SPOID)) {
                for(int i = 0;i < MAX_COLOR_PAGE;i++) {
                    if(colors.get(i+3).equals(clickColor)) {
                        colorPage = i;
                        break;
                    }
                }
                mode = ClickMode.NORMAL;
                refresh();
            } else if (mode.equals(ClickMode.PAINT)) {
                if(currentColor.equals(drawingSlot.get(slot).getPalette())) {
                    return;
                }
                int p_slot = slot;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(fill(p_slot / 7, p_slot % 7, drawingSlot.get(p_slot).getPalette(), 7)>0) { refresh(); }
                    }
                }.runTaskAsynchronously(main);
            } else if (mode.equals(ClickMode.NORMAL)) {
                drawingSlot.get(slot).setColor(currentColor);
                refresh();
            }
        } else if (slot == 0) {
            if(type.equals(ClickType.LEFT)) {
                if(colorPage <= 0) colorPage = MAX_COLOR_PAGE;
                else colorPage--;
                refresh();
            } else if(type.equals(ClickType.RIGHT)) {
                if(colorPage - 7 < 0) colorPage = MAX_COLOR_PAGE - 7 + colorPage + 1;
                else colorPage -= 7;
                refresh();
            }
        } else if (slot == 8) {
            if(type.equals(ClickType.LEFT)) {
                if (colorPage >= MAX_COLOR_PAGE) colorPage = 0;
                else colorPage++;
                refresh();
            }
            else if(type.equals(ClickType.RIGHT)) {
                if(colorPage + 7 > MAX_COLOR_PAGE) {
                    colorPage = (colorPage+7 - MAX_COLOR_PAGE) -1;
                }
                else colorPage += 7;
                refresh();
            }
        } else if (slot == 18) {
            if (drawPage <= 0) return;
            else {
                if(type.equals(ClickType.LEFT)) {
                    drawPage--;
                } else if(type.equals(ClickType.RIGHT)) {
                    if(drawPage - 7 < 0) drawPage = 0;
                    else drawPage -= 7;
                }
                refresh();
            }
        } else if (slot == 36) {
            if (drawPage >= MAX_DRAW_PAGE) return;
            else {
                if(type.equals(ClickType.LEFT)) drawPage++;
                else if(type.equals(ClickType.RIGHT)) {
                    drawPage = MAX_DRAW_PAGE;
                }
                refresh();
            }
        } else if (slot == 26) {
            String a = toString();
            islandData.setIcon(a);

            getPlayer().sendMessage("§7섬 아이콘이 [ §f" + a + " §7] 로 설정 되었습니다.");
            getPlayer().sendTitle(a, "§a섬 아이콘이 설정 되었습니다.", 0, 60, 20);
            getPlayer().closeInventory();
        } else if (slot == 44) {
            drawingSlot.clear();
            drawingSlot = new ArrayList<>();
            for (int j = 0; j < 7; j++)
                for (int i = 0; i < 7; i++) {
                    drawingSlot.add(new DrawIcon(j));
                }
            refresh();
        } else if (slot == 10) {
            mode = ClickMode.NORMAL;
            refresh();
        } else if (slot == 15) {
            mode = ClickMode.NORMAL;
            refresh();
        } else if (slot == 16) {
            mode = ClickMode.SPOID;
            refresh();
        } else if (slot == 17) {
            mode = ClickMode.PAINT;
            refresh();
        }
    }

    private DSIsland islandData;

    private boolean guildCheck() {
        return islandData.containsMember(DSCoreAPI.INSTANCE.getPlayerId(uuid));
    }

    private boolean check() {
        if(!guildCheck()) {
            getPlayer().closeInventory();
            getPlayer().sendMessage("§c섬 정보가 변경되어 창을 닫습니다.");
            islandData = null;
            return false;
        }

        return true;
    }

    private void setItem(String display, List<String> lore, Material m, int slot, int customModel) {
        ItemStack item = new ItemStack(m);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(display);
        if (customModel != 0) meta.setCustomModelData(customModel);
        if (lore != null) meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    public void closeInv() {
        remove();
    }

    private void remove() {
        guiMap.remove(uuid);
    }

    public static PixelGUI getGUI(Player p) {
        return guiMap.containsKey(p.getUniqueId()) ? guiMap.get(p.getUniqueId()) : null;
    }

    private Inventory inv;

    private Player getPlayer() {
        return server.getPlayer(uuid);
    }

    public String toString() {
        String res = drawingSlot.get(0).toString();
        for (int i = 1; i < 49; i++) {
            int line = i % 7;
            DrawIcon di = drawingSlot.get(i);
            res += (line == 0 ? upEscape : leftEscape) + di.toString();
        }
        return res;
    }

    private int fill(int line, int wide, Palette color, int max) {
        if (line < 0 || wide < 0 || wide >= max || line >= max) return 0;
        int slot = line * 7 + wide;
        DrawIcon di = drawingSlot.get(slot);
        if (!di.getPalette().equals(color)) return 0;

        int sum = 1;
        di.setColor(currentColor);

        sum += fill(line, wide + 1, color, max);
        sum += fill(line, wide - 1, color, max);
        sum += fill(line + 1, wide, color, max);
        sum += fill(line - 1, wide, color, max);
        return sum;
    }


}
