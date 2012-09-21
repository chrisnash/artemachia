package com.battlespace.main.viewer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.battlespace.domain.Coordinate;
import com.battlespace.domain.Deployment;
import com.battlespace.domain.ShipInstance;

public class ShellViewer implements Viewer
{
    // note deployment view is 2 characters wide per row, 8 characters for front rows
    // 26 characters, divider 26 characters
    public static final int DISPLAY_WIDTH = 53;
    public static final int DEPLOY_WIDTH = 26;
    public static final int DISPLAY_HEIGHT = 5;
    
    String[] display = new String[DISPLAY_HEIGHT];
    Deployment playerRef;
    Deployment enemyRef;
    Integer playerFront;
    Integer enemyFront;
    
    Map<Coordinate, Double> playerDamage;
    Map<Coordinate, Double> enemyDamage;
    
    @Override
    public void beginTurn(Deployment player, Deployment enemy)
    {
        playerRef = player;
        enemyRef = enemy;
        
        playerFront = player.frontLine();
        enemyFront = enemy.frontLine();
        
        playerDamage = new HashMap<Coordinate, Double>();
        enemyDamage = new HashMap<Coordinate, Double>();
        
        clear();
        String[] divider = fill(5, 1, "|");
        draw(divider, 0, DEPLOY_WIDTH);
        
        String[] render = render(player, true);
        draw(render, 0, DEPLOY_WIDTH - render[0].length());
        render = render(enemy, false);
        draw(render, 0, DEPLOY_WIDTH+1);
        
        show();
    }

    private void show()
    {
        for(int i=0;i<DISPLAY_HEIGHT;i++)
        {
            System.out.println(display[i]);
        }
        System.out.println();
    }

    private String[] render(Deployment player, boolean reverse)
    {
        String[] x = new String[DISPLAY_HEIGHT];
        
        Integer fl = player.frontLine();
        if(fl==null)
        {
            for(int i=0;i<DISPLAY_HEIGHT;i++) x[i]="";
            return x;
        }
        
        for(int r=0;r<DISPLAY_HEIGHT;r++)
        {
            StringBuffer sb = new StringBuffer();
            if(reverse)
            {
                for(int c=6;c>=fl;c--)
                {
                    boolean wide = (c<=fl+1);
                    ShipInstance si = player.getLivingShip(r, c);
                    if(si==null)
                    {
                        if(wide) sb.append("....... "); else sb.append(". ");
                    }
                    else
                    {
                        if(wide) sb.append(".." + si.getParent().getSize()+"-" + si.getUnits() + ".. ");
                        else sb.append(si.getParent().getSize()+" ");
                    }
                }
            }
            else
            {
                for(int c=fl;c<7;c++)
                {
                    boolean wide = (c<=fl+1);
                    ShipInstance si = player.getLivingShip(r, c);
                    if(si==null)
                    {
                        if(wide) sb.append(" ......."); else sb.append(" .");
                    }
                    else
                    {
                        if(wide) sb.append(" .." + si.getParent().getSize()+"-" + si.getUnits() + "..");
                        else sb.append(" " + si.getParent().getSize());
                    }
                }
            
            }
            x[r] = sb.toString();
        }
        return x;
    }

    private void draw(String[] sprite, int ro, int co)
    {
        for(int i=0; i<sprite.length; i++)
        {
            String s = sprite[i];
            int l = s.length();
            display[ro+i] = display[ro+i].substring(0, co) + s + display[ro+i].substring(co+l);
        }
    }

    private String[] fill(int r, int c, String string)
    {
        String filler = createString(string, c);
        String[] rv = new String[r];
        for(int i=0;i<r;i++)
        {
            rv[i] = filler;
        }
        return rv;
    }

    private String createString(String string, int c)
    {
        if(c==0) return "";
        if(c==1) return string;
        int h = (c>>1);
        String hs = createString(string, h);
        String x = ((h<<1)==c) ? "" : string;
        return hs+hs+x;
    }

    private void clear()
    {
        String filler = createString(" ", DISPLAY_WIDTH);
        for(int i=0;i<DISPLAY_HEIGHT;i++)
        {
            display[i] = filler;
        }
    }

    @Override
    public void recordDamage(Deployment victim, Coordinate target, double damage, List<Coordinate> attackers)
    {
        boolean player = (victim==playerRef);
        Map<Coordinate, Double> data = (player ? playerDamage : enemyDamage);
        Double old = data.get(target);
        if(old!=null) damage+=old;
        data.put(target, damage);
        //System.out.println((player?"player ":"enemy ") + target + " takes " + damage + " from " + attackers);
    }

    @Override
    public void endTurn(Deployment player, Deployment enemy)
    {
        renderDamage(playerDamage, true);
        renderDamage(enemyDamage, false);
        show();
    }

    private void renderDamage(Map<Coordinate, Double> damage, boolean isPlayer)
    {
        Deployment unitLookup = (isPlayer ? playerRef : enemyRef);
        Integer fl = (isPlayer ? playerFront : enemyFront);
        // The damage string has to be 7 characters wide
        for(Map.Entry<Coordinate, Double> e : damage.entrySet())
        {
            Coordinate k = e.getKey();
            Double v = e.getValue();
            int toShow = (int)Math.floor(v.doubleValue()+0.5);
            ShipInstance si = unitLookup.getLivingShip(k.r, k.c);
            int units = (si!=null) ? si.getUnits() : 0;
            
            String out = toShow + "(" + units + ")";
            while(out.length()<6)
            {
                out = "."+out+".";
            }
            while(out.length()<7)
            {
                out = out + "."; 
            }
            String[] sprite = new String[1];
            sprite[0] = out;
            // row is easy, column harder
            int columnOffset = (k.c - fl)*8;
            int c = isPlayer ? (DEPLOY_WIDTH - 8 - columnOffset) : (DEPLOY_WIDTH + 2 + columnOffset);
            draw(sprite, k.r, c);
        }
    }

}
