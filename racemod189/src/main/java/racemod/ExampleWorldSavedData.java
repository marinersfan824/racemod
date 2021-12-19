package racemod;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;



public class ExampleWorldSavedData extends WorldSavedData
{
  private static final String DATA_NAME = "msl-race-mod_ExampleData";
  public long pearlSeed;
  public long featherSeed;
  public long rodSeed;
	public long eyeSeed;
  
  public ExampleWorldSavedData() {
    super("msl-race-mod_ExampleData");
  }
  
  public ExampleWorldSavedData(String s) {
    super(s);
  }


  
  public void init(World world) {
    this.pearlSeed = world.getSeed() ^ 0x11101010L;
    this.featherSeed = world.getSeed() ^ 0x1111101101000010L;
    this.rodSeed = world.getSeed() ^ 0xF111B7010L;
	  this.eyeSeed = world.getSeed() ^ 0x99A2B75BBL;
    int i;
    for (i = 0; i < 17; i++)
    {
      updatePearlSeed();
    }
    
    for (i = 0; i < 43; i++)
    {
      updateFeatherSeed();
    }
    
    for (i = 0; i < 97; i++)
    {
      updateRodSeed();
    }
	  for (i = 0; i < 24; i++) {
		  updateEyeSeed();
	  }
  }

  
  public long updatePearlSeed() {
    long oldSeed = this.pearlSeed;
    
    Random random = new Random(oldSeed);
    this.pearlSeed = random.nextLong();
    
    markDirty();
    
    return oldSeed;
  }


  
  public long updateFeatherSeed() {
    long oldSeed = this.featherSeed;
    
    Random random = new Random(oldSeed);
    this.featherSeed = random.nextLong();
    
    markDirty();
    
    return oldSeed;
  }


  
  public long updateRodSeed() {
    long oldSeed = this.rodSeed;
    
    Random random = new Random(oldSeed);
    this.rodSeed = random.nextLong();
    
    markDirty();
    
    return oldSeed;
  }

	public long updateEyeSeed() {
		long oldSeed = this.eyeSeed;
		Random random = new Random(oldSeed);
		this.eyeSeed = random.nextLong();
		markDirty();
		return oldSeed;
	}


  
  public void readFromNBT(NBTTagCompound nbt) {
    System.out.println("WorldData readFromNBT");
    
    this.pearlSeed = nbt.getLong("pearlSeed");
    this.featherSeed = nbt.getLong("featherSeed");
    this.rodSeed = nbt.getLong("rodSeed");
    this.eyeSeed = nbt.getLong("eyeSeed");
  }

  public void writeToNBT(NBTTagCompound nbt) {
    nbt.setLong("pearlSeed", this.pearlSeed);
    nbt.setLong("featherSeed", this.featherSeed);
    nbt.setLong("rodSeed", this.rodSeed);
	  nbt.setLong("eyeSeed", this.eyeSeed);
  }

  
  public static ExampleWorldSavedData get(World world) {
    MapStorage storage = world.getPerWorldStorage();
    if (storage == null) {
      return null;
    }
    
    ExampleWorldSavedData result = null;

    try {
      result = (ExampleWorldSavedData)storage.loadData(ExampleWorldSavedData.class, "msl-race-mod_ExampleData");
    } catch (Exception exception) {

      
      try {
        result = (ExampleWorldSavedData)storage.getClass().getMethod("getOrLoadData", new Class[] { Class.class, String.class }).invoke(storage, new Object[] { WorldSavedData.class, "msl-race-mod_ExampleData" });
      }
      catch (Exception exception1) {}
    } 
    
    if (result == null) {
      result = new ExampleWorldSavedData("msl-race-mod_ExampleData");
      result.init(world);
      storage.setData("msl-race-mod_ExampleData", result);
    } 
    return result;
  }
  /**
   * Simulates the RNG for pearls, blazerods, feathers, and eye breaks. Significantly
   * speeds up the process of manually checking rates on a seed.
   * 
   * @param world - the world 
   */
  public static void tellPlayerInitialRates(World world) {
	  ExampleWorldSavedData dummy = new ExampleWorldSavedData("msl-race-mod_ExampleData");
	  dummy.init(world);
	  int total_blazerods = 0;
	  int total_blazes = 0;
	  int total_pearls = 0;
	  int total_endermen = 0;
	  int broken_eyes = 0;
	  int total_eyes = 0;
	  while (total_blazerods < 6) {
		  int seedResult = Math.abs((int)dummy.updateRodSeed()) % (int)Math.pow(16.0D, 4.0D);
	      boolean didPass = (seedResult % 16 < 8);  
	      if (didPass) {
	        total_blazerods++;
	      }
	      total_blazes++;
	  }
      while (total_pearls < 12) {
    	  int seedResult = Math.abs((int)dummy.updatePearlSeed()) % (int)Math.pow(16.0D, 4.0D);
    	  boolean didPass = (seedResult % 16 < 10);  
    	  if (didPass) {
    		  total_pearls++;
    	  }
    	  total_endermen++;
      }
      while (total_eyes < 5) {
    	  int seedResult = Math.abs((int)dummy.updateEyeSeed()) % (int)Math.pow(16.0D, 4.0D);
    	  boolean didPass = (seedResult % 5 > 0);
    	  if (!didPass) {
    		  broken_eyes++;
    	  }
    	  total_eyes++;
      }
      EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
	  player.sendChatMessage(String.format("Blaze rates are %d/%d, Endermen "
	  		+ "rates are %d/%d, eye breaks are %d/%d", 
	  		total_blazerods, total_blazes, total_pearls, total_endermen, broken_eyes, total_eyes));
	  world.playSoundEffect(player.posX, player.posY, player.posZ, "ambient.weather.thunder", 10000.0F, 0.8F + 0.2F);
  }

}