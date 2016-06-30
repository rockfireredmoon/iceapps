__AnimationDefs = __AnimationDefs;

with(JavaImporter(org.icescene.scene)) {
	__AnimationDefs["Biped"] = {
		blend : 0.2,
		alignToFloorMode : AlignToFloorMode.NONE,
		categories : {
			AttackLinked : {
				top : 900,
				bottom : 900
			},
			Death : {
				top : 1000,
				bottom : 1000
			},
			Idle : {
				top : 100,
				bottom : 100
			},
			Movement : {
				top : 400,
				bottom : 700
			},
			AttackSeperable : {
				top : 800,
				bottom : 600
			},
			IdleTurn : {
				top : 200,
				bottom : 500
			},
			EmoteLinked : {
				top : 300,
				bottom : 200
			},
			Hit : {
				top : 600,
				bottom : 300
			},
			Jump : {
				top : 500,
				bottom : 800
			},
			EmoteSeperable : {
				top : 700,
				bottom : 400
			}
		},
		animations : { 
			One_Handed_Jumping_Thrust : {
				impacts : [ 0.5 ],
				category : "AttackLinked"
			},
			Magic_Humble : {
				category : "AttackSeperable"
			},
			One_Handed_Side_Draw : {
				category : "AttackSeperable"
			},
			Staff_Spinning_Smash : {
				impacts : [ 0.65 ],
				category : "AttackLinked"
			},
			Walk_Strafe_Left : {
				blend : 0.1,
				category : "Movement",
				stepSounds : [ 0.25, 0.75 ]
			},
			Bow_Attack_Release_v2 : {
				blend : 0.1,
				category : "EmoteSeperable"
			},
			Dual_Wield_Side_Cuts : {
				impacts : [ 0.3, 0.5 ],
				category : "AttackSeperable"
			},
			Blow_Kiss : {
				hideWeapons : true,
				category : "EmoteSeperable"
			},
			One_Handed_Attack1 : {
				impacts : [ 0.3 ],
				category : "AttackSeperable"
			},
			Sneak_Jab1 : {
				blend : 0.2,
				category : "AttackSeperable",
				impacts : [ 0.3, 0.43, 0.65 ]
			},
			Curtsey : {
				hideWeapons : true,
				category : "EmoteLinked"
			},
			Walk34_R : {
				blend : 0.1,
				category : "Movement",
				stepSounds : [ 0.25, 0.75 ]
			},
			One_Handed_Attack2 : {
				impacts : [ 0.4 ],
				category : "AttackSeperable"
			},
			Dance_Staying_Alive : {
				hideWeapons : true,
				category : "EmoteLinked"
			},
			Sneak_Eviscerate1 : {
				blend : 0.2,
				category : "AttackSeperable",
				impacts : [ 0.2, 0.4 ]
			},
			Flex : {
				hideWeapons : true,
				category : "EmoteSeperable"
			},
			Two_Handed_Side_Cuts : {
				impacts : [ 0.29, 0.63 ],
				category : "AttackSeperable"
			},
			Dual_Wield_Badgers_Embrace : {
				impacts : [ 0.2, 0.4, 0.6 ],
				category : "AttackSeperable"
			},
			Staff_Spinning_Sweep : {
				impacts : [ 0.5 ],
				category : "AttackLinked"
			},
			Laugh : {
				hideWeapons : true,
				category : "EmoteSeperable"
			},
			Death_Sideways : {
				category : "Death"
			},
			Idle_Combat_Two_Handed : {
				category : "Idle"
			},
			Jog34_Back_L : {
				blend : 0.1,
				category : "Movement",
				stepSounds : [ 0.25, 0.75 ]
			},
			Bow : {
				hideWeapons : true,
				category : "EmoteLinked"
			},
			Idle_Combat_Bow : {
				blend : 0.1,
				category : "Idle"
			},
			Sneak_Hamstring3 : {
				blend : 0.2,
				category : "AttackLinked",
				impacts : [ 0.2, 0.4 ]
			},
			Sneak_Hamstring1 : {
				blend : 0.2,
				category : "AttackLinked",
				impacts : [ 0.2, 0.4 ]
			},
			Jog34_Back_R : {
				blend : 0.1,
				category : "Movement",
				stepSounds : [ 0.25, 0.75 ]
			},
			Idle_Female : {
				category : "Idle"
			},
			Two_Handed_Spinning : {
				impacts : [ 0.47 ],
				category : "AttackLinked"
			},
			Sneak_Midcross3 : {
				blend : 0.2,
				category : "AttackSeperable",
				impacts : [ 0.4 ]
			},
			Sneak_Midcross2 : {
				blend : 0.2,
				category : "AttackSeperable",
				impacts : [ 0.4 ]
			},
			Jog_Strafe_Right : {
				blend : 0.1,
				category : "Movement",
				stepSounds : [ 0.25, 0.75 ]
			},
			Dual_Wield_Back_Sheathe : {
				category : "AttackSeperable"
			},
			Point : {
				category : "EmoteSeperable"
			},
			Jog34_L : {
				blend : 0.1,
				category : "Movement",
				stepSounds : [ 0.25, 0.75 ]
			},
			Jog34_R : {
				blend : 0.1,
				category : "Movement",
				stepSounds : [ 0.25, 0.75 ]
			},
			One_Handed_Back_Sheathe : {
				category : "EmoteSeperable"
			},
			Magic_Spellgasm : {
				category : "AttackLinked"
			},
			Sneak_Midcross1 : {
				blend : 0.2,
				category : "AttackSeperable",
				impacts : [ 0.4 ]
			},
			Dance_Raise_The_Roof : {
				hideWeapons : true,
				category : "EmoteLinked"
			},
			Idle_Rotate_Left : {
				category : "IdleTurn"
			},
			One_Handed_Side_Sheathe : {
				category : "AttackSeperable"
			},
			Hit_Dual_Wield : {
				category : "Hit"
			},
			Dance_Conga : {
				hideWeapons : true,
				category : "EmoteLinked"
			},
			Idle_Rotate_Right : {
				category : "IdleTurn"
			},
			Staff_Smash : {
				impacts : [ 0.36 ],
				category : "AttackSeperable"
			},
			Hammer_Kneel : {
				category : "AttackLinked"
			},
			Jog_Strafe_Left : {
				blend : 0.1,
				category : "Movement",
				stepSounds : [ 0.25, 0.75 ]
			},
			Jump_Standing_Recover : {
				blend : 0.1,
				category : "Jump"
			},
			Hit_Staff : {
				category : "Hit"
			},
			Pose1 : {
				category : "EmoteLinked"
			},
			Bow_Attack_Release : {
				blend : 0.1,
				category : "AttackSeperable"
			},
			Dual_Wield_Figure_8 : {
				impacts : [ 0.2, 0.4, 0.6, 0.8 ],
				category : "AttackSeperable"
			},
			Hit_Bow : {
				category : "Hit"
			},
			Run : {
				blend : 0.1,
				category : "Movement",
				stepSounds : [ 0.25, 0.75 ]
			},
			Hammer_Stand : {
				category : "AttackSeperable"
			},
			Two_Handed_Diagonal2 : {
				impacts : [ 0.275, 0.55 ],
				category : "AttackSeperable"
			},
			Walk_Backward : {
				blend : 0.1,
				category : "Movement",
				stepSounds : [ 0.25, 0.75 ]
			},
			Dual_Wield_Dagger_Draw : {
				category : "EmoteSeperable"
			},
			Walk34_Back_L : {
				blend : 0.1,
				category : "Movement",
				stepSounds : [ 0.25, 0.75 ]
			},
			Dual_Wield_Diagonal : {
				impacts : [ 0.4 ],
				category : "AttackSeperable"
			},
			One_Handed_Diagonal_Strike : {
				impacts : [ 0.2, 0.5 ],
				category : "AttackSeperable"
			},
			Dual_Wield_Middle_Cross : {
				impacts : [ 0.3, 0.5 ],
				category : "AttackSeperable"
			},
			Dig_Pick : {
				hideWeapons : true,
				category : "EmoteSeperable"
			},
			Swim_R : {
				blend : 0.1,
				category : "Movement"
			},
			Walk34_Back_R : {
				blend : 0.1,
				category : "Movement",
				stepSounds : [ 0.25, 0.75 ]
			},
			Swim_L : {
				blend : 0.1,
				category : "Movement"
			},
			Staff_Underhand : {
				impacts : [ 0.2, 0.55 ],
				category : "AttackSeperable"
			},
			Walk_Strafe_Right : {
				blend : 0.1,
				category : "Movement",
				stepSounds : [ 0.25, 0.75 ]
			},
			Sad : {
				hideWeapons : true,
				category : "EmoteSeperable"
			},
			Death_Forward : {
				category : "Death"
			},
			Dual_Wield_Back_Draw : {
				category : "AttackSeperable"
			},
			Hit_Two_Handed : {
				category : "Hit"
			},
			Worship : {
				hideWeapons : true,
				category : "EmoteSeperable"
			},
			Exclaim : {
				hideWeapons : true,
				category : "EmoteSeperable"
			},
			Wave : {
				hideWeapons : true,
				category : "EmoteSeperable"
			},
			Walk34_L : {
				blend : 0.1,
				category : "Movement",
				stepSounds : [ 0.25, 0.75 ]
			},
			Walk : {
				blend : 0.1,
				category : "Movement",
				stepSounds : [ 0.25, 0.75 ]
			},
			Swim_Tread : {
				blend : 0.1,
				category : "Movement"
			},
			Victory : {
				category : "AttackSeperable"
			},
			Jog_Backward : {
				blend : 0.1,
				category : "Movement",
				stepSounds : [ 0.25, 0.75 ]
			},
			Two_Handed_Thunder_Maul : {
				impacts : [ 0.51 ],
				category : "AttackLinked"
			},
			Two_Handed_Furys_Eye : {
				impacts : [ 0.21, 0.33, 0.46, 0.59 ],
				category : "AttackLinked"
			},
			Two_Handed_Diagonal1 : {
				impacts : [ 0.275, 0.55 ],
				category : "AttackSeperable"
			},
			Applaud : {
				hideWeapons : true,
				category : "EmoteSeperable"
			},
			Swim_Forward_R : {
				blend : 0.1,
				category : "Movement"
			},
			Magic_Wand_Point : {
				category : "AttackSeperable"
			},
			Swim_Forward_L : {
				blend : 0.1,
				category : "Movement"
			},
			Swim_Forward : {
				blend : 0.1,
				category : "Movement"
			},
			Sneak_Eviscerate2 : {
				blend : 0.2,
				category : "AttackSeperable",
				impacts : [ 0.2, 0.4 ]
			},
			Staff_SideToSide : {
				impacts : [ 0.23, 0.5 ],
				category : "AttackSeperable"
			},
			Sneak_Throw2 : {
				blend : 0.2,
				category : "AttackSeperable"
			},
			Idle_Combat_1h : {
				category : "Idle"
			},
			Sneak_Throw4 : {
				blend : 0.2,
				category : "AttackSeperable"
			},
			Sneak_Throw3 : {
				blend : 0.2,
				category : "AttackSeperable"
			},
			Kick1 : {
				impacts : [ 0.2 ],
				category : "AttackLinked"
			},
			Sneak_Hamstring2 : {
				blend : 0.2,
				category : "AttackLinked",
				impacts : [ 0.2, 0.4 ]
			},
			Proclaim : {
				hideWeapons : true,
				category : "EmoteSeperable"
			},
			Interact : {
				hideWeapons : true,
				category : "EmoteSeperable"
			},
			Spinning_Daggers : {
				blend : 0.2,
				category : "AttackLinked",
				impacts : [ 0.3, 0.4, 0.5, 0.6, 0.75 ]
			},
			Spinning_Claws : {
				blend : 0.2,
				category : "AttackLinked",
				impacts : [ 0.3, 0.4, 0.5, 0.6, 0.75 ]
			},
			Sneak_Throw1 : {
				blend : 0.2,
				category : "AttackSeperable"
			},
			Dual_Wield_Dagger_Sheathe : {
				category : "EmoteSeperable"
			},
			Sneak_Jab3 : {
				blend : 0.2,
				category : "AttackSeperable",
				impacts : [ 0.3, 0.43, 0.65 ]
			},
			Sneak_Jab2 : {
				blend : 0.2,
				category : "AttackSeperable",
				impacts : [ 0.3, 0.43, 0.65 ]
			},
			Staff_Side_Jabs : {
				impacts : [ 0.2, 0.575 ],
				category : "AttackSeperable"
			},
			Sneak_Eviscerate3 : {
				blend : 0.2,
				category : "AttackSeperable",
				impacts : [ 0.2, 0.4 ]
			},
			Swim_Back : {
				blend : 0.1,
				category : "Movement"
			},
			Death_Backward : {
				category : "Death"
			},
			Magic_Double_Handed : {
				hideWeapons : true,
				category : "AttackSeperable"
			},
			Shrug : {
				hideWeapons : true,
				category : "EmoteSeperable"
			},
			Magic_Prayer : {
				category : "AttackLinked"
			},
			Run34_R : {
				blend : 0.1,
				category : "Movement",
				stepSounds : [ 0.25, 0.75 ]
			},
			Beg : {
				hideWeapons : true,
				category : "EmoteSeperable"
			},
			Jump_Standing_Top : {
				blend : 0.1,
				category : "Jump"
			},
			Potion_Drink : {
				hideWeapons : true,
				category : "EmoteSeperable"
			},
			One_Handed_Spinning_Backswing : {
				impacts : [ 0.5 ],
				category : "AttackLinked"
			},
			Dig_Shovel : {
				hideWeapons : true,
				category : "EmoteSeperable"
			},
			One_Handed_Shield_Counter : {
				impacts : [ 0.2, 0.45 ],
				category : "AttackLinked"
			},
			Run34_L : {
				blend : 0.1,
				category : "Movement",
				stepSounds : [ 0.25, 0.75 ]
			},
			Jump_Standing_Down : {
				blend : 0.5,
				category : "Jump"
			},
			Idle_Combat_Staff : {
				category : "Idle"
			},
			Dual_Wield_Diagonal2 : {
				impacts : [ 0.4, 0.6 ],
				category : "AttackSeperable"
			},
			One_Handed_Back_Draw : {
				category : "EmoteSeperable"
			},
			Magic_Wand_Channel : {
				category : "AttackSeperable"
			},
			Magic_Throw : {
				category : "AttackSeperable"
			},
			Dual_Wield_Flashy_Strike : {
				impacts : [ 0.4 ],
				category : "AttackSeperable"
			},
			Magic_Spell_Channel : {
				category : "AttackSeperable"
			},
			Salute : {
				hideWeapons : true,
				category : "EmoteSeperable"
			},
			Magic_Casting : {
				hideWeapons : true,
				category : "AttackSeperable"
			},
			One_Handed_Flashy : {
				impacts : [ 0.5 ],
				category : "AttackSeperable"
			},
			Jump_Standing_Up : {
				blend : 0.1,
				category : "Jump"
			},
			Jog : {
				blend : 0.1,
				category : "Movement",
				stepSounds : [ 0.25, 0.75 ]
			},
			Spinning_Katars : {
				blend : 0.2,
				category : "AttackLinked",
				impacts : [ 0.3, 0.4, 0.5, 0.6, 0.75 ]
			},
			Idle_Combat_Magic : {
				category : "Idle"
			},
			Idle : {
				category : "Idle"
			},
			Float_Walk : {
				blend : 0.1,
				category : "Movement"
			},
			Excited : {
				hideWeapons : true,
				category : "EmoteLinked"
			},
			Dual_Wield_Jump_Flip : {
				impacts : [ 0.2, 0.7 ],
				category : "AttackLinked"
			}
		},
		actions : { 
			Swim_Forward : {
				Swim_Forward : [ 80, 100, 10000 ]
			},
			Forward_Right : {
				Walk34_R : [ 0, 25, 40 ],
				Jog34_R : [ 40, 45, 50 ],
				Run34_R : [ 50, 80, 10000 ]
			},
			Backward_Right : {
				Walk34_Back_L : [ 0, 20, 20 ],
				Jog34_Back_R : [ 20, 35, 10000 ]
			},
			Swim_Forward_Left : {
				Swim_Forward_L : [ 80, 100, 10000 ]
			},
			Swim_Forward_Right : {
				Swim_Forward_R : [ 80, 100, 10000 ]
			},
			Backward : {
				Jog_Backward : [ 20, 35, 10000 ],
				Walk_Backward : [ 0, 20, 20 ]
			},
			Swim_Right : {
				Swim_R : [ 80, 100, 10000 ]
			},
			StrafeRight : {
				Jog_Strafe_Right : [ 40, 50, 10000 ],
				Walk_Strafe_Right : [ 0, 20, 40 ]
			},
			StrafeLeft : {
				Jog_Strafe_Left : [ 40, 50, 10000 ],
				Walk_Strafe_Left : [ 0, 20, 40 ]
			},
			Backward_Left : {
				Jog34_Back_L : [ 20, 35, 10000 ],
				Walk34_Back_R : [ 0, 20, 20 ]
			},
			Forward_Left : {
				Run34_L : [ 50, 80, 10000 ],
				Walk34_L : [ 0, 25, 40 ],
				Jog34_L : [ 40, 45, 50 ]
			},
			Swim_Left : {
				Swim_L : [ 80, 100, 10000 ]
			},
			Forward : {
				Walk : [ 0, 24, 40 ],
				Run : [ 50, 80, 10000 ],
				Jog : [ 40, 45, 50 ]
			},
			Swim_Backward : {
				Swim_Back : [ 25, 50, 10000 ]
			}
		}
	};
}
