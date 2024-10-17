package net.botwithus;

import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.game.Area;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.inventories.Backpack;
import net.botwithus.rs3.game.login.LoginManager;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.LoopingScript;
import net.botwithus.rs3.script.config.ScriptConfig;

import java.util.Objects;
import java.util.Random;

public class SkeletonScript extends LoopingScript {

    // Varbits
//    kElvenShard = 40606,
//    kExcalibur = 22838,
//    kOverload = 48834,
//    kAdrenalinePotion = 47576,
//    kAggression = 33448,
//    kWeaponPoison = 2102,
//    kCharmingPotion = 45518,
//    kJujuFarming = 26021,
//    kSorceryPowerBurst = 45523,
//    kPowerBurstCooldown = 45519,
//    kVitalityPowerBurst = 45364,
//    kExtremeRunecrafting = 41918,
//    kAntifire = 498,
//    kElderScripture = 30605, // Seems to apply to all GWD3 scriptures (Active/Non-active)
//    kBossHealth = 28663,
//    kAura = 22900, // Non-specific type
//    kShieldTier = 22842,
//    kSpiritWeedIncense = 43695, // 1-4. 4 = Overloaded
//    kKwuarmIncense = 43707, // 1-4. 4 = Overloaded
//    kSpiritWeedIncenseTime = 43713, // 0 - 200
//    kKwuarmIncenseTime = 43725, // 0 - 200
//    kSignOfLife = 20147,
//    kGrandExchangeTab = 19000,
//    kPassiveVigour = 4318,
//    kMarksOfWar = 45689,
//    kEarnedMarks = 45690,
//    kMarkCooldown = 45691,
//    kVulnerability = 1939,
//    kAnimateDead = 49447,
//    kEnchantmentOfDispelling = 41465,
//    kEnchantmentOfShadows = 51481,
//    kGreaterDeathsSwiftness = 51566,
//    kGreaterSunshine = 51567,
//    kPlantedFeet = 30983,
//    kGreaterRicochet = 48719,
//    kBladedDive = 35826,
//    kGreaterDazingShot = 35816,
//    kSacrifice = 21067,
//    kDevotion = 21068,
//    kDoubleSurge = 44244, // 44246 went from 0 -> 2 after unlocking as well
//    kVampyrismAura = 39185, // Aura unlocked
//    kDesolation = 34891,
//    kLividFarmSpell = 16374, // 1 = one spell unlocked, 2 = two, 7 = Disruption Shield
//    kFamiliarHealth = 19034,
//    kLastGuardianStacks = 51511,
//    kSmokeCloud = 49448,
//    kDivert = 48721,
//    kFamiliarFireRate = 25413,
//    kWrackAndRuin = 49568,
//    kCombatStance = 1899,
//    kTurtlingRank = 30340,
//    kBorrowedPowerCharges = 38568,
//    kVengeance = 2,
//    kVengeanceCooldown = 3,
//    kDisruptionShield = 9,
//    kDisruptionCooldown = 10,
//    kBonfireHealthBoost = 28800,
//    kKalgerionRoar = 28800,
//    kRevengeStacks = 1898,

    private BotState botState = BotState.IDLE;
    private boolean someBool = true;
    private Random random = new Random();
    private int noSuppliesCheckAttempts = 0;
    public int randomEvents = 0;
    enum BotState {
        //define your own states here
        IDLE,
        SKILLING,
        BANKING,
        REPAIRING_ALL
        //...
    }

    public SkeletonScript(String s, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(s, scriptConfig, scriptDefinition);
        this.sgc = new SkeletonScriptGraphicsContext(getConsole(), this);

    }

    @Override
    public void onLoop() {
        //Loops every 100ms by default, to change:
        //this.loopDelay = 500;
        LocalPlayer player = Client.getLocalPlayer();
        if (player == null || Client.getGameState() != Client.GameState.LOGGED_IN || botState == BotState.IDLE) {
            //wait some time so we dont immediately start on login.
            Execution.delay(random.nextLong(3000,7000));
            return;
        }
        switch (botState) {
            case IDLE -> {
                //do nothing
                println("We're idle!");
                if(player.getTarget() != null) {
                    println(player.getTarget().getName() + ": Animation Id: " + player.getTarget().getAnimationId());
                }
                Execution.delay(random.nextLong(1000, 3000));
            }
            case SKILLING -> {
                //do some code that handles your skilling
                Execution.delay(handleSkilling(player));
            }
            case BANKING -> {
                //handle your banking logic, etc
            }
            case REPAIRING_ALL -> {

            }
        }
    }

    public boolean logout() {
        println("Logging out...");
        if (Client.getGameState() != Client.GameState.LOGGED_IN) {
            return true;
        }
        LoginManager.setAutoLogin(false);
//        MiniMenu.interact(ComponentAction.COMPONENT.getType(), 1, -1, 93913156);
        var settingsButton = ComponentQuery.newQuery(1431).componentIndex(0).text("Settings").option("Open").results();
        if (settingsButton.size() != 1) {
            println("Settings button not found.");
            return false;
        }
        Objects.requireNonNull(settingsButton.first()).interact("Open");
        if (!Execution.delayUntil(5000, () -> Interfaces.isOpen(1433))) {
            println("Failed to open settings interface.");
            return false;
        }

        var logoutButton = ComponentQuery.newQuery(1433).componentIndex(71).results();
        println("logout quantity: " + logoutButton.size());
        if (logoutButton.size() != 1) {
            println("Logout button not found.");
            return false;
        }
        Objects.requireNonNull(logoutButton.first()).interact("Select");

        // check for "Are you sure if you want to log out?" dialogue

        var confirmationDialogue = ComponentQuery.newQuery(1433).componentIndex(85).results();
        if (confirmationDialogue.size() != 1) {
            println("Confirmation dialogue not found.");
        }
        else {
            Objects.requireNonNull(confirmationDialogue.first()).interact("Select");
        }

        boolean logoutResult = Execution.delayUntil(5000, () -> Client.getGameState() != Client.GameState.LOGGED_IN);
        if (!logoutResult) {
            println("Failed to log out.");
        }
        else {
            println("Logged out.");
        }
        return logoutResult;
    }

    private long handleSkilling(LocalPlayer player) {
        // Define the area where to search (e.g., between coordinates)
        Coordinate bottomLeft = new Coordinate(1043, 1771, 1); // Define bottom-left corner
        Coordinate topRight = new Coordinate(1033, 1781, 1); // Define top-right corner
        Area searchArea = new Area.Rectangular(bottomLeft, topRight);

//        player.getTarget().getAnimationId();

        //Checks if no ritual is currently in progress.
        boolean noRitual = (VarManager.getVarbitValue(53292) == 0);

        if(noSuppliesCheckAttempts >= 2) {
            setBotState(BotState.IDLE);
            println("No supplies found.");
            if(logout()) {
                setBotState(BotState.IDLE);
                return random.nextLong(1000, 3000);
            }
        }

        // If moving between pedestal & platform wait.
        if(player.isMoving()) {
            println("Player is moving please wait...");
            Execution.delayUntil(5000, () -> !player.isMoving());
            return random.nextLong(2000, 2500);
        }

        // Create the NPC query confined within the defined area
        EntityResultSet<Npc> npcsinArea = NpcQuery.newQuery().inside(searchArea).results();

        if(VarManager.getVarbitValue(53292) == 0) {
            Npc depleted = null;
            for (Npc npc : npcsinArea) {
                if (Objects.requireNonNull(npc.getName()).contains("depleted")) {
                    depleted = npc;
                    break;
                }
            }
            if (depleted != null) {
                println(depleted.getName());
                SceneObject pedestal = SceneObjectQuery.newQuery().name("Pedestal (powerful communion)").option("Repair all").results().first();
                if (pedestal != null) {
                    noSuppliesCheckAttempts++;
                    println("Repaired All: " + pedestal.interact("Repair all"));
                    return random.nextLong(1500, 3000);
                }

            }
        }

        SceneObject platform2 = SceneObjectQuery.newQuery().name("Platform").option("Continue ritual").results().nearest();

        if(VarManager.getVarbitValue(53292) > 0) {
            for (Npc npc : npcsinArea) {
                if(npc == null)
                    continue;
                if (npc.getName().contains("Wandering soul")) {
                    println("Name: " + npc.getName());
                    println("Soul Dismissed: " + npc.interact("Dismiss"));
                    randomEvents++;
                    Execution.delay(random.nextLong(1250, 1750));
                } else if (npc.getName().contains("Sparkling glyph")) {
                    println("Name: " + npc.getName());
                    println("Glyph Restored: " + npc.interact("Restore"));
                    randomEvents++;
                    Execution.delay(random.nextLong(1250, 1750));
                    Coordinate randomCoord = searchArea.getRandomWalkableCoordinate();
                    if(randomCoord != null) {
                        Movement.walkTo(randomCoord.getX(), randomCoord.getY(), false);
                        Execution.delayUntil(5000, () -> !player.isMoving());
                    }
                } else if (platform2 != null && platform2.getCoordinate().getX() != player.getServerCoordinate().getX()  && player.getAnimationId() == -1 && !player.isMoving()) {
                    noSuppliesCheckAttempts = 0;
                    print("Interact with Platform Continue Ritual: " + platform2.interact("Continue ritual"));
                    Execution.delayUntil(5000, () -> !player.isMoving());
                }

            }
        }

        SceneObject platform = SceneObjectQuery.newQuery().name("Platform").option("Start ritual").results().nearest();
        if (platform != null && player.getAnimationId() != 35520) {
            noSuppliesCheckAttempts = 0;
            println("Interacted with platform: " + platform.interact("Start ritual"));
            return random.nextLong(1000, 3000);
        }

        return random.nextLong(1500, 3000);
    }

//        if (Interfaces.isOpen(1251))
//            return random.nextLong(250,1500);
//        //if our inventory is full, lets bank.
//        if (Backpack.isFull()) {
//            println("Going to banking state!");
//            botState = BotState.BANKING;
//            return random.nextLong(250,1500);
//        }
//        //click my tree, mine my rock, etc...
//        SceneObject tree = SceneObjectQuery.newQuery().name("Tree").option("Chop").results().nearest();
//        if (tree != null) {
//            println("Interacted tree: " + tree.interact("Chop"));
//        }
    public BotState getBotState() {
        return botState;
    }

    public void setBotState(BotState botState) {
        this.botState = botState;
    }

    public boolean isSomeBool() {
        return someBool;
    }

    public void setSomeBool(boolean someBool) {
        this.someBool = someBool;
    }
}
