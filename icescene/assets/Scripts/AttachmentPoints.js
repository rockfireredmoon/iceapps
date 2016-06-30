__AttachmentPoints = __AttachmentPoints;
__AttachableTemplates = __AttachableTemplates;

with (JavaImporter(com.jme3.math, org.icescene.math,
		org.icescene.configuration.attachments, org.icelib.beans)) {

	__AttachmentPoints["Biped"] = {
		key : "Biped",
		places : {
			base : {
				bone : "Parent",
				position : new Vector3f(0.0, 5.0, 0.0)
			},
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 2.0, 0)
			},
			left_hand : {
				bone : "Bone-LeftHand",
				position : new Vector3f(1.2, 0.25999999, 0.5),
				orientation : ExtMath.orientation(-0.02, 0.0, 0.0)
			},
			left_hand2 : {
				bone : "Bone-LeftHand",
				position : new Vector3f(1.5, 0.25999999, 2.3499999)
			},
			left_hand_Weapons : {
				bone : "Bone-LeftHand",
				position : new Vector3f(1.2, 0.25999999, 0.5),
				orientation : ExtMath.orientation(-0.47499999, 0.0, 0.0)
			},
			left_hand_Shield : {
				bone : "Bone-LeftForearm",
				position : new Vector3f(2.25, 0.25999999, -0.34999999),
				orientation : ExtMath.orientation(0, -0.029999999, 0)
			},
			right_hand : {
				bone : "Bone-RightHand",
				position : new Vector3f(1.2, 0.25999999, -0.5),
				orientation : ExtMath.orientation(-0.02, 0, 0)
			},
			right_hand_Weapons : {
				bone : "Bone-RightHand",
				position : new Vector3f(1.2, 0.40000001, -0.5),
				orientation : ExtMath.orientation(0.97000003, 0, 0)
			},
			right_hand2 : {
				bone : "Bone-RightHand",
				position : new Vector3f(1.5, 0.25999999, -2.3499999)
			},
			right_hand_Arrow : {
				bone : "Bone-RightHand",
				position : new Vector3f(8.0, 0.25999999, -0.5),
				orientation : ExtMath.orientation(0, 0.75, 0)
			},
			right_hand_Staff : {
				bone : "Bone-RightHand",
				position : new Vector3f(1.2, 0.25999999, -0.5),
				orientation : ExtMath.orientation(-0.02, 0, 0)
			},
			left_arm : {
				bone : "Bone-LeftArm"
			},
			left_shoulder : {
				bone : "Bone-LeftArm"
			},
			right_arm : {
				bone : "Bone-RightArm"
			},
			right_shoulder : {
				bone : "Bone-RightArm"
			},
			left_forearm : {
				bone : "Bone-LeftForearm"
			},
			left_forearm_Weapons : {
				bone : "Bone-LeftForearm",
				position : new Vector3f(0, 0.0, 0.0),
				orientation : ExtMath.orientation(0, 0, 0)
			},
			right_forearm : {
				bone : "Bone-RightForearm"
			},
			left_calf : {
				bone : "Bone-LeftCalf"
			},
			right_calf : {
				bone : "Bone-RightCalf01"
			},
			tail : {
				bone : "Bone-TailEnd"
			},
			head : {
				bone : "Bone-Head"
			},
			head2 : {
				bone : "Bone-Head"
			},
			hat : {
				bone : "Bone-Head"
			},
			helmet : {
				bone : "Bone-Head",
				hidden : [ "left_ear", "right_ear", "head2" ]
			},
			left_earring : {
				bone : "Bone-LeftEar"
			},
			right_earring : {
				bone : "Bone-RightEar"
			},
			left_ear : {
				bone : "Bone-LeftEar"
			},
			right_ear : {
				bone : "Bone-RightEar"
			},
			back_pack : {
				bone : "Bone-Back",
				position : new Vector3f(2.7, -1.15, 0.0),
				orientation : ExtMath.orientation(0, 0, 0.75)
			},
			ctf : {
				bone : "Bone-Back",
				position : new Vector3f(0.0, 0.0, 0.0),
				orientation : ExtMath.orientation(0.75, 0, 0.75)
			},
			back_arrows : {
				bone : "Bone-Back",
				position : new Vector3f(2.7, -1.15, 0.0),
				orientation : ExtMath.orientation(0, 0, 0.75)
			},
			back_1 : {
				bone : "Bone-Back",
				position : new Vector3f(2.0, -1.9, 1.0),
				orientation : ExtMath.orientation(0.07, -0.025, 0.74000001)
			},
			back_1_2hSwords : {
				bone : "Bone-Back",
				position : new Vector3f(11.5, -3.2, 1.75),
				orientation : ExtMath.orientation(0.1, 0.98000002, 0.23999999)
			},
			back_1_swords : {
				bone : "Bone-Back",
				position : new Vector3f(7.1999998, -2.45, 1.5),
				orientation : ExtMath.orientation(0.1, -0.0099999998, 0.245)
			},
			back_1_staffs : {
				bone : "Bone-Back",
				position : new Vector3f(3.0, -2.3, 1.2),
				orientation : ExtMath.orientation(0.25, -0.0099999998,
						0.75999999)
			},
			back_2 : {
				bone : "Bone-Back",
				position : new Vector3f(2.0, -1.9, -1.0),
				orientation : ExtMath.orientation(-0.07, 0.025, 0.74000001)
			},
			back_2_bows : {
				bone : "Bone-Back",
				scale : new Vector3f(0.60000002, 0.80000001, 0.80000001),
				position : new Vector3f(2.9000001, -2.0999999, -1.3),
				orientation : ExtMath.orientation(0.75, 0.0099999998, 0.28)
			},
			back_2_2hSwords : {
				bone : "Bone-Back",
				position : new Vector3f(11.5, -3.2, -1.75),
				orientation : ExtMath
						.orientation(-0.1, -0.98000002, 0.23999999)
			},
			back_2_swords : {
				bone : "Bone-Back",
				position : new Vector3f(7.1999998, -2.45, -1.5),
				orientation : ExtMath.orientation(0.89999998, 0.0099999998,
						0.245)
			},
			back_2_staffs : {
				bone : "Bone-Back",
				position : new Vector3f(3.0, -2.3, -1.2),
				orientation : ExtMath.orientation(0.25, 0.0099999998,
						0.75999999)
			},
			back_3 : {
				bone : "Bone-Back",
				position : new Vector3f(4.5, -2.8699999, 0.55000001),
				orientation : ExtMath.orientation(0.25, 0.97799999, -0.75)
			},
			back_4 : {
				bone : "Bone-Back",
				position : new Vector3f(4.5, -2.8699999, -0.55000001),
				orientation : ExtMath.orientation(0.75, 0.022, -0.75)
			},
			back_5 : {
				bone : "Bone-Back",
				position : new Vector3f(9.6999998, -3.7, -2.7),
				orientation : ExtMath.orientation(0.75, 0.025, 0.31999999)
			},
			back_5_Special1 : {
				bone : "Bone-Back",
				position : new Vector3f(9.6999998, -3.7, -2.7),
				orientation : ExtMath.orientation(0.75, 0.025, 0.31999999)
			},
			back_5_Special2 : {
				bone : "Bone-Back",
				position : new Vector3f(0.0, -2.3, -0.77999997),
				orientation : ExtMath.orientation(0.75, 0.025, 0.69999999)
			},
			back_6 : {
				bone : "Bone-Back",
				position : new Vector3f(2.5999999, -2.0, 0.0),
			},

			// -----------------------------------------------
			// Attachment Points for 2H Equipped Items
			// -----------------------------------------------

			back_2hsword_equip : {
				bone : "Bone-Back",
				position : new Vector3f(7.0999999, -2.5, -1.8),
				orientation : ExtMath.orientation(0.75, 0.0099999998,
						0.31999999)
			},
			back_Pole_equip : {
				bone : "Bone-Back",
				position : new Vector3f(2.0, -2.0999999, 0.69999999),
				orientation : ExtMath.orientation(0.75, 0.0099999998, -0.17)
			},
			back_AxeMace_equip : {
				bone : "Bone-Back",
				position : new Vector3f(0.0, -2.0999999, 0.69999999),
				orientation : ExtMath.orientation(0.75, 0.015, -0.17)
			},
			// -----------------------------------------------
			// End Attachment Points for 2H Equipped Items
			// -----------------------------------------------

			chest : {
				bone : "Bone-Back"
			},
			chest2 : {
				bone : "Bone-Back",
				position : new Vector3f(2.7, 2.1500001, 0.0)
			},
			chest3 : {
				bone : "Bone-Back",
				position : new Vector3f(2.7, 0.0, 0.0)
			},
			spell_target : {
				bone : "Bone-Back",
				position : new Vector3f(2.7, 0.0, 0.0)
			},
			spell_target_head : {
				bone : "Bone-Head",
				position : new Vector3f(1.5, 0, 0)
			},
			spell_symbole : {
				bone : "Bone-Head",
				position : new Vector3f(5.0, 5.0, 0.0)
			},
			spell_origin : {
				bone : "Bone-RightHand"
			},
			daze_stun : {
				bone : "Bone-Head",
				position : new Vector3f(5.0, 0, 0)
			},
			center_hip : {
				bone : "Bone-Hips",
				position : new Vector3f(0.0, 0.0, 0.0),
				orientation : ExtMath.orientation(0.0, 0.0, 0.25)
			},
			left_hip : {
				bone : "Bone-Hips",
				position : new Vector3f(-0.2, 1.6, -2.0999999),
				orientation : ExtMath
						.orientation(0.51499999, 0.0, -0.085000001)
			},
			right_hip : {
				bone : "Bone-Hips",
				position : new Vector3f(-0.2, 1.6, 2.0999999),
				orientation : ExtMath.orientation(-0.51499999, 0.0,
						-0.085000001)
			},
			left_hip_swords : {
				bone : "Bone-Hips",
				position : new Vector3f(-0.2, 1.6, -2.0999999),
				orientation : ExtMath
						.orientation(0.51499999, 0.0, -0.085000001)
			},
			right_hip_swords : {
				bone : "Bone-Hips",
				position : new Vector3f(-0.2, 1.6, 2.0999999),
				orientation : ExtMath.orientation(-0.51499999, 0.0,
						-0.085000001)
			},
			left_hip_wands : {
				bone : "Bone-Hips",
				position : new Vector3f(0.0, -0.30000001, -2.0),
				orientation : ExtMath
						.orientation(0.51499999, 0.0, -0.085000001)
			},
			right_hip_wands : {
				bone : "Bone-Hips",
				position : new Vector3f(0.0, -0.30000001, 2.0),
				orientation : ExtMath.orientation(-0.51499999, 0.0,
						-0.085000001)
			},
			left_hip_axes : {
				bone : "Bone-Hips",
				position : new Vector3f(-0.2, 1.0, -2.0999999),
				orientation : ExtMath
						.orientation(0.51499999, 0.0, -0.085000001)
			},
			right_hip_axes : {
				bone : "Bone-Hips",
				position : new Vector3f(-0.2, 1.0, 2.0999999),
				orientation : ExtMath.orientation(-0.51499999, 0.0,
						-0.085000001)
			},
			left_hip_daggers : {
				bone : "Bone-Hips",
				position : new Vector3f(-0.2, 0.40000001, -2.0999999),
				orientation : ExtMath
						.orientation(0.51499999, 0.0, -0.097000003)
			},
			right_hip_daggers : {
				bone : "Bone-Hips",
				position : new Vector3f(-0.2, 0.40000001, 2.0999999),
				orientation : ExtMath.orientation(-0.51499999, 0.0,
						-0.097000003)
			},
			left_hip_katars : {
				bone : "Bone-LeftThigh",
				position : new Vector3f(0.0, 1.0, -0.15000001),
				orientation : ExtMath.orientation(-0.25, 0.0, 0.0)
			},
			right_hip_katars : {
				bone : "Bone-RightThigh01",
				position : new Vector3f(0.0, 1.0, 0.15000001),
				orientation : ExtMath.orientation(0.75, 0.0, 0.0)
			},
			left_hip_claws : {
				bone : "Bone-LeftThigh",
				position : new Vector3f(0.0, 1.0, -0.15000001),
				orientation : ExtMath.orientation(-0.25, 0.0, 0.0)
			},
			right_hip_claws : {
				bone : "Bone-RightThigh01",
				position : new Vector3f(0.0, 1.0, 0.15000001),
				orientation : ExtMath.orientation(-0.25, 0.0, 0.0)
			},
			head_particles : {
				bone : "Bone-Head",
				position : new Vector3f(0.0, 0.0, 0.0),
				orientation : ExtMath.orientation(-0.25, 0.0, -0.25)
			},
			right_foot : {
				bone : "Bone-RightFoot01"
			},
			left_foot : {
				bone : "Bone-LeftFoot"
			}
		}
	};

	// Since our meshes are calibrated to the male size, we don't bother
	// tweaking them here.
	__AttachmentPoints["Biped.Male"] = {
		key : "Biped.Male",
		places : __AttachmentPoints["Biped"].places
	};

	// Shoulders need tweaking for female.
	__AttachmentPoints["Biped.Female"] = {
		key : "Biped.Female",
		places : ObjectMapping.delegate(__AttachmentPoints["Biped"].places, {
			left_shoulder : {
				bone : "Bone-LeftArm",
				scale : new Vector3f(0.89999998, 0.89999998, 0.89999998),
				position : new Vector3f(-0.30000001, 0.0, 0.2)
			},
			right_shoulder : {
				bone : "Bone-RightArm",
				scale : new Vector3f(0.89999998, 0.89999998, 0.89999998),
				position : new Vector3f(-0.30000001, 0.0, -0.2)
			}
		})
	};

	// Templates
	__AttachableTemplates["Item.2hAxes"] = {
		attachPoints : [ "right_hand", "back_1", "back_2", "back_sheathe" ],
		attachAliases : {
			back_sheathe : "back_AxeMace_equip"
		},
		ribbon : {}
	};

	__AttachableTemplates["Item.2hAxes_Special1"] = {
		attachPoints : [ "right_hand", "back_5", "back_sheathe" ],
		attachAliases : {
			back_5 : "back_5_Special2",
			back_sheathe : "back_AxeMace_equip"
		},
		ribbon : {}
	};
	__AttachableTemplates["Item.Arrows"] = {
		attachPoints : [ "back_arrows" ]
	};
	__AttachableTemplates["Item.2hSwords"] = {
		attachPoints : [ "right_hand", "back_1", "back_2", "back_sheathe" ],
		attachAliases : {
			back_1 : "back_1_2hSwords",
			back_2 : "back_2_2hSwords",
			back_sheathe : "back_2hsword_equip"
		},
		ribbon : {}
	};
	__AttachableTemplates["Item.2hSwords_Special1"] = {
		attachPoints : [ "right_hand", "back_5", "back_sheathe" ],
		attachAliases : {
			back_5 : "back_5_Special1",
			back_sheathe : "back_2hsword_equip"
		},
		ribbon : {}
	};
	__AttachableTemplates["Item.1hSwords"] = {
		attachPoints : [ "right_hand", "left_hand", "back_1", "back_2",
				"left_hip", "right_hip" ],
		attachAliases : {
			back_1 : "back_1_swords",
			back_2 : "back_2_swords",
			left_hip : "left_hip_swords",
			right_hip : "right_hip_swords"
		},
		ribbon : {}
	};
	__AttachableTemplates["Item.1hSwords_backless"] = {
		attachPoints : [ "right_hand", "left_hand", "left_hip", "right_hip" ],
		attachAliases : {
			left_hip : "left_hip_swords",
			right_hip : "right_hip_swords"
		},
		ribbon : {}
	};
	__AttachableTemplates["Item.Wands"] = {
		attachPoints : [ "right_hand", "left_hand", "left_hip", "right_hip" ],
		attachAliases : {
			left_hip : "left_hip_wands",
			right_hip : "right_hip_wands"
		},
		ribbon : {}
	};
	__AttachableTemplates["Item.Talismans"] = {
		attachPoints : [ "right_hand", "left_hand", "left_hip", "right_hip" ],
		attachAliases : {
			left_hip : "left_hip_wands",
			right_hip : "right_hip_wands"
		}
	};
	__AttachableTemplates["Item.TalismansB"] = {
		attachPoints : [ "right_hand", "left_hand", "left_hip", "right_hip" ],
		attachAliases : {
			left_hip : "left_hip_claws",
			right_hip : "right_hip_claws",
			left_hand : "left_hand_Weapons"
		}
	};
	__AttachableTemplates["Item.1hAxes"] = {
		attachPoints : [ "right_hand", "left_hand", "left_hip", "right_hip" ],
		attachAliases : {
			left_hip : "left_hip_axes",
			right_hip : "right_hip_axes"
		},
		ribbon : {}
	};
	__AttachableTemplates["Item.Bows"] = {
		attachPoints : [ "left_hand", "back_2" ],
		attachAliases : {
			back_2 : "back_2_bows"
		}
	};
	__AttachableTemplates["Item.Daggers"] = {
		attachPoints : [ "right_hand", "left_hand", "left_hip", "right_hip",
				"back_3", "back_4" ],
		attachAliases : {
			left_hip : "left_hip_daggers",
			right_hip : "right_hip_daggers"
		},
		ribbon : {}
	};
	__AttachableTemplates["Item.Katars"] = {
		attachPoints : [ "right_hand", "left_hand", "left_hip", "right_hip" ],
		attachAliases : {
			left_hip : "left_hip_katars",
			right_hip : "right_hip_katars",
			left_hand : "left_hand_Weapons"
		},
		ribbon : {}
	};
	__AttachableTemplates["Item.HandClaws"] = {
		attachPoints : [ "right_hand", "left_hand", "left_hip", "right_hip" ],
		attachAliases : {
			left_hip : "left_hip_claws",
			right_hip : "right_hip_claws",
			left_hand : "left_hand_Weapons",
			right_hand : "right_hand_Weapons"
		},
		ribbon : {}
	};
	__AttachableTemplates["Item.WristClaws"] = {
		attachPoints : [ "right_hand", "left_hand", "left_hip", "right_hip" ],
		attachAliases : {
			left_hip : "left_hip_claws",
			right_hip : "right_hip_claws",
			left_hand : "left_forearm_Weapons",
			right_hand : "right_forearm"
		},
		ribbon : {}
	};
	__AttachableTemplates["Item.1hMaces"] = {
		attachPoints : [ "right_hand", "left_hand", "left_hip", "right_hip" ],
		attachAliases : {
			left_hip : "left_hip_wands",
			right_hip : "right_hip_wands"
		},
		ribbon : {}
	};
	__AttachableTemplates["Item.2hMaces"] = {
		attachPoints : [ "right_hand", "back_1", "back_2", "back_sheathe" ],
		attachAliases : {
			back_sheathe : "back_AxeMace_equip"
		},
		ribbon : {}
	};
	__AttachableTemplates["Item.Shields"] = {
		attachPoints : [ "left_hand", "back_6" ],
		attachAliases : {
			left_hand : "left_hand_Shield"
		}
	};
	__AttachableTemplates["Item.Staffs"] = {
		attachPoints : [ "right_hand", "back_1", "back_2", "back_5",
				"back_sheathe" ],
		attachAliases : {
			right_hand : "right_hand_Staff",
			back_1 : "back_1_staffs",
			back_2 : "back_2_staffs",
			back_5 : "back_5_Special1",
			back_sheathe : "back_Pole_equip"
		},
		ribbon : {}
	};
	__AttachableTemplates["Item.Backpack"] = {
		attachPoints : [ "back_pack", "crab_shell" ]
	};
	__AttachableTemplates["Item.CTF"] = {
		attachPoints : [ "ctf" ]
	};
	__AttachableTemplates["Item.Earring"] = {
		attachPoints : [ "left_earring", "right_earring" ]
	};
	__AttachableTemplates["Item.LeftPauldron"] = {
		attachPoints : [ "left_shoulder" ]
	};
	__AttachableTemplates["Item.RightPauldron"] = {
		attachPoints : [ "right_shoulder" ]
	};
	__AttachableTemplates["Item.RightGauntlet"] = {
		attachPoints : [ "right_forearm" ]
	};
	__AttachableTemplates["Item.LeftGauntlet"] = {
		attachPoints : [ "left_forearm" ]
	};
	__AttachableTemplates["Item.RightGreave"] = {
		attachPoints : [ "right_calf" ]
	};
	__AttachableTemplates["Item.LeftGreave"] = {
		attachPoints : [ "left_calf" ]
	};
	__AttachableTemplates["Item.Helmet"] = {
		attachPoints : [ "helmet" ]
	};
	__AttachableTemplates["Item.Headdress"] = {
		attachPoints : [ "helmet" ]
	};
	__AttachableTemplates["Item.FullHelmet"] = {
		attachPoints : [ "full_helmet" ]
	};
	__AttachableTemplates["Item.Hat"] = {
		attachPoints : [ "hat" ]
	};
	__AttachableTemplates["Item.Pot"] = {
		attachPoints : [ "pot" ]
	};
	__AttachableTemplates["Item.BunnyEars"] = {
		attachPoints : [ "bunnyears" ]
	};
	__AttachableTemplates["Item.Gorget"] = {
		attachPoints : [ "chest" ]
	};
	__AttachableTemplates["Item.Base"] = {
		attachPoints : [ "base" ]
	};
	__AttachableTemplates["Particle.Head"] = {
		attachPoints : [ "head_particles" ],
		particle : true
	};
	__AttachableTemplates["Particle.Body"] = {
		attachPoints : [ "chest" ],
		particle : true
	};
	__AttachableTemplates["Particle.Hips"] = {
		attachPoints : [ "center_hip" ],
		particle : true
	};
	__AttachableTemplates["Particle.Base"] = {
		attachPoints : [ "base" ],
		particle : true
	};
	__AttachableTemplates["Particle.Shoulders"] = {
		attachPoints : [ "left_shoulder", "right_shoulder" ],
		particle : true
	};
	__AttachableTemplates["Particle.Wings"] = {
		attachPoints : [ "left_elbow", "right_elbow" ],
		particle : true
	};
	__AttachableTemplates["Particle.Phoenix"] = {
		attachPoints : [ "left_elbow", "right_elbow", "left_shoulder",
				"right_shoulder", "head_particles", "center_hip" ],
		particle : true
	};
	__AttachableTemplates["Particle.Tail"] = {
		attachPoints : [ "tail" ],
		particle : true
	};

	//
	// Creatures
	//
	__AttachmentPoints["Biter"] = {
		key : "Biter",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 50.0, 0),
				scale : new Vector3f(2.0, 2.0, 2.0)
			},
			spell_origin : {
				bone : "Bone-Stick01",
				scale : new Vector3f(1.1, 1.1, 1.1)
			},
			spell_target : {
				bone : "Bone-Stick01",
				scale : new Vector3f(1.1, 1.1, 1.1)
			},
			horde_attacker : {
				bone : "Bone-Stick01",
				scale : new Vector3f(1.1, 1.1, 1.1)
			},
			horde_caster : {
				bone : "Bone-Stick01",
				scale : new Vector3f(1.1, 1.1, 1.1),
				orientation : ExtMath.orientation(0, 0.25, 0)
			},
			daze_stun : {
				bone : "Bone-Stick01",
				scale : new Vector3f(1.1, 1.1, 1.1)
			},
			head_particles : {
				bone : "Bone04"
			}
		}
	};

	__AttachmentPoints["Wandering_Eye"] = {
		key : "Wandering_Eye",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 50.0, 0),
				scale : new Vector3f(2.0, 2.0, 2.0)
			},
			spell_origin : {
				bone : "Bone-Stick01",
				scale : new Vector3f(1.1, 1.1, 1.1)
			},
			spell_target : {
				bone : "Bone-Stick01",
				scale : new Vector3f(1.1, 1.1, 1.1)
			},
			horde_attacker : {
				bone : "Bone-Stick01",
				scale : new Vector3f(1.1, 1.1, 1.1)
			},
			horde_caster : {
				bone : "Bone-Stick01",
				scale : new Vector3f(1.1, 1.1, 1.1)
			},
			daze_stun : {
				bone : "Bone-Stick01",
				scale : new Vector3f(1.1, 1.1, 1.1)
			}
		}
	};

	__AttachmentPoints["Shroomie"] = {
		key : "Shroomie",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 20.0, 0)
			},
			spell_target : {
				bone : "Bone-Back"
			},
			spell_origin : {
				bone : "Bone-Back"
			},
			spell_target_head : {
				bone : "Bone-Head",
				position : new Vector3f(1.5, 0, 0)
			},
			helmet : {
				bone : "Bone-Head",
				position : new Vector3f(3, 0, 0)
			},
			horde_attacker : {
				bone : "Bone-RightForearm",
				position : new Vector3f(3, 0, 0)
			},
			horde_caster : {
				bone : "Bone-RightForearm",
				position : new Vector3f(3, 0, 0),
				orientation : ExtMath.orientation(0, 0.25, 0)
			},
			right_hand : {
				bone : "Bone-RightForearm",
				position : new Vector3f(1.5, 0.25999999, -0.34999999),
				orientation : ExtMath.orientation(0.025, 0.0, 0)
			},
			left_shoulder : {
				bone : "Bone-LeftArm"
			},
			right_shoulder : {
				bone : "Bone-RightArm"
			},
			left_hand_Shield : {
				bone : "Bone-LeftForearm",
				position : new Vector3f(1, 0.1, -0.25),
				orientation : ExtMath.orientation(0.0, -0.029999999, 0)
			},
			daze_stun : {
				bone : "Bone-Head",
				position : new Vector3f(5.0, 0, 0)
			}
		}
	};

	__AttachmentPoints["Wolf"] = {
		key : "Wolf",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 20.0, 0)
			},
			spell_target : {
				bone : "head"
			},
			spell_origin : {
				bone : "head"
			},
			spell_target_head : {
				bone : "head"
			},
			center_hip : {
				bone : "back02"
			},
			left_shoulder : {
				bone : "shoulder_L"
			},
			right_shoulder : {
				bone : "shoulder_R"
			},
			horde_attacker : {
				bone : "head",
				position : new Vector3f(5.0, 0, 2.0)
			},
			horde_caster : {
				bone : "head",
				position : new Vector3f(1.0, 0.0, -5.0),
				orientation : ExtMath.orientation(0.0, 0.25, 0)
			},
			daze_stun : {
				bone : "head",
				position : new Vector3f(1.0, 0.0, -5.0),
				orientation : ExtMath.orientation(0.0, 0.25, 0)
			}
		}
	};

	__AttachmentPoints["Flytrap"] = {
		key : "Flytrap",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 20.0, 0)
			},
			spell_origin : {
				bone : "Bone07"
			},
			spell_target : {
				bone : "Bone07"
			},
			horde_attacker : {
				bone : "Bone07"
			},
			spell_target_head : {
				bone : "Bone07"
			},
			horde_caster : {
				bone : "Bone07",
				position : new Vector3f(1.0, 0.0, -3.0),
				orientation : ExtMath.orientation(0.0, 0.30000001, 0)
			},
			daze_stun : {
				bone : "Bone07",
				position : new Vector3f(1.0, 0.0, -3.0),
				orientation : ExtMath.orientation(0.0, 0.30000001, 0)
			}
		}
	};

	__AttachmentPoints["Treekin"] = {
		key : "Treekin",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 32.0, 0)
			},
			spell_target : {
				bone : "Bone06"
			},
			spell_origin : {
				bone : "Bone06"
			},
			horde_attacker : {
				bone : "L_Bone13"
			},
			horde_attacker2 : {
				bone : "R_Bone13"
			},
			head_particles : {
				bone : "Bone06"
			},
			spell_target_head : {
				bone : "Bone06"
			},
			horde_caster : {
				bone : "Bone06",
				position : new Vector3f(16.0, 0.0, 0.0),
				orientation : ExtMath.orientation(0.75, 0.0, 0)
			},
			daze_stun : {
				bone : "Bone06",
				position : new Vector3f(16.0, 0.0, 0.0)
			},
			left_shoulder : {
				bone : "L_Bone10"
			},
			right_shoulder : {
				bone : "R_Bone10"
			}
		}
	};

	__AttachmentPoints["Krostakin"] = {
		key : "Krostakin",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 18.0, 0)
			},
			spell_target : {
				bone : "Bone-Back"
			},
			spell_origin : {
				bone : "Bone-Back"
			},
			spell_target_head : {
				bone : "Bone-Back"
			},
			horde_attacker : {
				bone : "Bone-RightHand"
			},
			horde_caster : {
				bone : "Bone-RightHand",
				orientation : ExtMath.orientation(0.0, 0.2, 0)
			},
			daze_stun : {
				bone : "Bone-Back",
				position : new Vector3f(8.0, -4.0, 0.0),
				orientation : ExtMath.orientation(0.0, 0.0, 0.85000002)
			}
		}
	};

	__AttachmentPoints["Boar"] = {
		key : "Boar",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 14.0, 0)
			},
			spell_target : {
				bone : "Head"
			},
			spell_origin : {
				bone : "Head"
			},
			spell_target_head : {
				bone : "Head"
			},
			horde_attacker : {
				bone : "Head",
				position : new Vector3f(5, 0, 0)
			},
			horde_caster : {
				bone : "Head",
				position : new Vector3f(-2.0, 0.0, 5.0),
				orientation : ExtMath.orientation(0.0, 0.2, 0)
			},
			daze_stun : {
				bone : "Head",
				position : new Vector3f(-2.0, 0.0, 5.0),
				orientation : ExtMath.orientation(0.0, 0.60000002, 0)
			}
		}
	};

	__AttachmentPoints["Vampire"] = {
		key : "Vampire",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 18.0, 0)
			},
			spell_origin : {
				bone : "Bone-Back"
			},
			spell_target : {
				bone : "Bone-Back"
			},
			spell_target_head : {
				bone : "Bone-Back"
			},
			horde_attacker : {
				bone : "Bone-LeftHand"
			},
			horde_attacker2 : {
				bone : "Bone-RightHand"
			},
			horde_caster : {
				bone : "Bone-RightHand",
				orientation : ExtMath.orientation(0.0, 0.2, 0)
			},
			daze_stun : {
				bone : "Bone-Back",
				position : new Vector3f(9.0, 0.0, 0.0),
				orientation : ExtMath.orientation(0.0, 0.05000000, 0)
			}
		}
	};

	__AttachmentPoints["Vespin"] = {
		key : "Vespin",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 28.0, 0)
			},
			spell_origin : {
				bone : "Bone02"
			},
			spell_target : {
				bone : "Bone02"
			},
			spell_target_head : {
				bone : "Bone02"
			},
			horde_attacker : {
				bone : "Bone06"
			},
			horde_caster : {
				bone : "Bone02",
				position : new Vector3f(-4.0, 0.0, 0.0),
				orientation : ExtMath.orientation(0.0, 0.1, 0)
			},
			daze_stun : {
				bone : "Bone02",
				position : new Vector3f(4.0, 0.0, -0.2),
				orientation : ExtMath.orientation(0.0, 0.1, 0)
			}
		}
	};

	__AttachmentPoints["Simian"] = {
		key : "Simian",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 20.0, 0)
			},
			spell_origin : {
				bone : "Bone-Head"
			},
			spell_target : {
				bone : "Bone-Head"
			},
			spell_target_head : {
				bone : "Bone-Head"
			},
			horde_attacker : {
				bone : "Bone-LeftHand"
			},
			horde_attacker2 : {
				bone : "Bone-RightHand"
			},
			horde_caster : {
				bone : "Bone-Head",
				position : new Vector3f(4.5, 0.0, 0.0),
				orientation : ExtMath.orientation(0.69999999, 0, 0)
			},
			daze_stun : {
				bone : "Bone-Head",
				position : new Vector3f(4.5, 0.0, 0.0),
				orientation : ExtMath.orientation(0, 0, 0.94999999)
			}
		}
	};

	__AttachmentPoints["ThunderWalker"] = {
		key : "ThunderWalker",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 20.0, 0)
			},
			spell_origin : {
				bone : "Bone04",
				position : new Vector3f(3.0, 0.0, 0)
			},
			spell_target : {
				bone : "Bone04",
				position : new Vector3f(3.0, 0.0, 0)
			},
			spell_target_head : {
				bone : "Bone04",
				position : new Vector3f(3.0, 0.0, 0)
			},
			horde_attacker : {
				bone : "Bone04",
				position : new Vector3f(4.0, 0.0, -10.0),
				orientation : ExtMath.orientation(0, 0.25, 0)
			},
			horde_caster : {
				bone : "Bone04",
				position : new Vector3f(7.0, 0.0, -7.0),
				orientation : ExtMath.orientation(0, 0.25, 0)
			},
			daze_stun : {
				bone : "Bone04",
				position : new Vector3f(4.0, 0.0, -10.0),
				orientation : ExtMath.orientation(0, 0.25, 0)
			}
		}
	};

	__AttachmentPoints["ThunderWalker2"] = {
		key : "ThunderWalker2",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 30.0, 0)
			},
			spell_origin : {
				bone : "Bone04",
				position : new Vector3f(3.0, 0.0, 0)
			},
			spell_target : {
				bone : "Bone04",
				position : new Vector3f(3.0, 0.0, 0)
			},
			center_hip : {
				bone : "Bone02",
				position : new Vector3f(-7.0, 0.0, -7.0)
			},
			spell_target_head : {
				bone : "Bone04",
				position : new Vector3f(3.0, 0.0, 0)
			},
			horde_attacker : {
				bone : "Bone04",
				position : new Vector3f(4.0, 0.0, -10.0),
				orientation : ExtMath.orientation(0, 0.25, 0)
			},
			horde_caster : {
				bone : "Bone04",
				position : new Vector3f(7.0, 0.0, -7.0),
				orientation : ExtMath.orientation(0, 0.25, 0)
			},
			daze_stun : {
				bone : "Bone04",
				position : new Vector3f(4.0, 0.0, -10.0),
				orientation : ExtMath.orientation(0, 0.25, 0)
			}
		}
	};

	__AttachmentPoints["Spider"] = {
		key : "Spider",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 25.0, 0)
			},
			spell_origin : {
				bone : "Bone-Head"
			},
			spell_target : {
				bone : "Bone-Head"
			},
			spell_target_head : {
				bone : "Bone-Head"
			},
			horde_attacker : {
				bone : "Bone-Head",
				position : new Vector3f(5, 0, 0)
			},
			horde_caster : {
				bone : "Bone-Head",
				position : new Vector3f(0.0, 4.0, 0.0),
				orientation : ExtMath.orientation(0, 0.25, 0)
			},
			crab_shell : {
				bone : "Bone-Hips"
			},
			daze_stun : {
				bone : "Bone-Head",
				position : new Vector3f(0.0, 4.0, 0.0),
				orientation : ExtMath.orientation(0, 0, 0.30000001)
			}
		}
	};

	__AttachmentPoints["Spibear"] = {
		key : "Spibear",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 15.0, 0)
			},
			spell_origin : {
				bone : "Head"
			},
			spell_target : {
				bone : "Head"
			},
			center_hip : {
				bone : "Back2"
			},
			spell_target_head : {
				bone : "Head"
			},
			horde_attacker : {
				bone : "Head",
				position : new Vector3f(5, 0, 0)
			},
			horde_caster : {
				bone : "Head",
				position : new Vector3f(-0.1, 0.0, -6.0),
				orientation : ExtMath.orientation(0, 0.34999999, 0)
			},
			daze_stun : {
				bone : "Head",
				position : new Vector3f(-0.1, 0.0, -6.0),
				orientation : ExtMath.orientation(0, 0.34999999, 0)
			}
		}
	};

	__AttachmentPoints["Rabbit"] = {
		key : "Rabbit",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 6.0, 0)
			},
			spell_origin : {
				bone : "Head"
			},
			spell_target : {
				bone : "Head"
			},
			spell_target_head : {
				bone : "Head"
			},
			horde_caster : {
				bone : "Head",
				position : new Vector3f(0.0, 0, -3.0),
				orientation : ExtMath.orientation(0, 0.30000001, 0)
			},
			horde_attacker : {
				bone : "Head"
			},
			daze_stun : {
				bone : "Head",
				position : new Vector3f(0.0, 0, -3.0),
				orientation : ExtMath.orientation(0, 0.30000001, 0)
			}
		}
	};

	__AttachmentPoints["Elemental"] = {
		key : "Elemental",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 30.0, 0)
			},
			base : {
				bone : "Parent"
			},
			spell_origin : {
				bone : "Head"
			},
			spell_target : {
				bone : "Head"
			},
			spell_target_head : {
				bone : "Head"
			},
			horde_attacker : {
				bone : "L_Hand"
			},
			horde_attacker2 : {
				bone : "R_Hand"
			},
			horde_caster : {
				bone : "Back3",
				position : new Vector3f(1.75, 0.0, 1.25),
				orientation : ExtMath.orientation(0, 0.1, 0)
			},
			horde_back : {
				bone : "Back3",
				position : new Vector3f(1.75, 0.0, 1.25),
				orientation : ExtMath.orientation(0.25, 0, 0.15000001)
			},
			daze_stun : {
				bone : "Head",
				position : new Vector3f(-2.0, 0.0, -3.0),
				orientation : ExtMath.orientation(0, 0.34999999, 0)
			},
			center_hip : {
				bone : "Floater_Base"
			},
			left_shoulder : {
				bone : "L_UpperArm",
				orientation : ExtMath.orientation(0, 0.5, 0)
			},
			right_shoulder : {
				bone : "R_UpperArm",
				orientation : ExtMath.orientation(0, 0.5, 0)
			}
		}
	};

	__AttachmentPoints["Salamander"] = {
		key : "Salamander",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 15.0, 0)
			},
			spell_origin : {
				bone : "Head"
			},
			spell_target : {
				bone : "Head"
			},
			spell_target_head : {
				bone : "Head"
			},
			tail : {
				bone : "Tail3",
				position : new Vector3f(5.0, 0.0, 0.0)
			},
			horde_attacker : {
				bone : "Head",
				position : new Vector3f(5, 0, 0)
			},
			horde_caster : {
				bone : "Head",
				position : new Vector3f(0.0, 0.0, -5.0),
				orientation : ExtMath.orientation(0, 0.30000001, 0)
			},
			daze_stun : {
				bone : "Head",
				position : new Vector3f(0.0, 0.0, -5.0),
				orientation : ExtMath.orientation(0, 0.30000001, 0)
			}
		}
	};

	__AttachmentPoints["Chicken"] = {
		key : "Chicken",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 15.0, 0)
			},
			spell_origin : {
				bone : "Head"
			},
			spell_target : {
				bone : "Head"
			},
			spell_target_head : {
				bone : "Head"
			},
			horde_caster : {
				bone : "Head",
				position : new Vector3f(0.0, 0.0, -5.0),
				orientation : ExtMath.orientation(0, 0.25, 0)
			},
			horde_attacker : {
				bone : "Head"
			},
			daze_stun : {
				bone : "Head",
				position : new Vector3f(0.0, 0.0, -5.0),
				orientation : ExtMath.orientation(0, 0.25, 0)
			}
		}
	};

	__AttachmentPoints["Stag"] = {
		key : "Stag",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 25.0, 0)
			},
			spell_origin : {
				bone : "Stag-Head"
			},
			spell_target : {
				bone : "Stag-Head"
			},
			head_particles : {
				bone : "Stag-Head",
				position : new Vector3f(-3.0, 0, 0.0),
				orientation : ExtMath.orientation(0.75, 0, 0)
			},
			horde_attacker : {
				bone : "Stag-Head"
			},
			horde_caster : {
				bone : "Stag-Head",
				position : new Vector3f(0.0, 0, -5.0),
				orientation : ExtMath.orientation(0, 0.30000001, 0)
			},
			daze_stun : {
				bone : "Stag-Head",
				position : new Vector3f(0.0, 0, -5.0),
				orientation : ExtMath.orientation(0, 0.30000001, 0)
			}
		}
	};

	__AttachmentPoints["Behemoth"] = {
		key : "Behemoth",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 35.0, 0)
			},
			spell_origin : {
				bone : "Back02"
			},
			spell_target : {
				bone : "Back02"
			},
			spell_target_head : {
				bone : "Head"
			},
			horde_caster : {
				bone : "Head",
				position : new Vector3f(0.0, 0, -3.5),
				orientation : ExtMath.orientation(0, 0.34999999, 0)
			},
			horde_attacker : {
				bone : "L_Finger2"
			},
			horde_attacker2 : {
				bone : "R_Hand"
			},
			daze_stun : {
				bone : "Head",
				position : new Vector3f(0.0, 0, -3.5),
				orientation : ExtMath.orientation(0, 0.34999999, 0)
			}
		}
	};

	__AttachmentPoints["Vulture"] = {
		key : "Vulture",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 30.0, 0),
				size : new Vector3f(0.89999998, 0.89999998, 0.89999998)
			},
			spell_origin : {
				bone : "Bone-Neck01"
			},
			spell_target : {
				bone : "Bone-Neck01"
			},
			spell_target_head : {
				bone : "Bone-Head"
			},
			horde_attacker : {
				bone : "Bone-Head"
			},
			horde_caster : {
				bone : "Bone-Neck01",
				position : new Vector3f(0.0, 0.0, -7.0),
				orientation : ExtMath.orientation(0.40000001, 0, 0)
			},
			left_shoulder : {
				bone : "Bone-LeftArm",
				position : new Vector3f(2.0, 0.0, 1.0),
				orientation : ExtMath.orientation(0, 0, 0)
			},
			right_shoulder : {
				bone : "Bone-RightArm",
				position : new Vector3f(2.0, 0.0, -1.0),
				orientation : ExtMath.orientation(0, 0, 0)
			},
			left_elbow : {
				bone : "Bone-L_Finger02",
				position : new Vector3f(0.0, 0.0, 1.0),
				orientation : ExtMath.orientation(0, 0, 0)
			},
			center_hip : {
				bone : "Bone-Back",
				position : new Vector3f(-2.0, 0.0, 0.0),
				orientation : ExtMath.orientation(0, 0.5, 0)
			},
			right_elbow : {
				bone : "Bone-R_Finger02",
				position : new Vector3f(0.0, 0.0, -1.0),
				orientation : ExtMath.orientation(0, 0, 0)
			},
			daze_stun : {
				bone : "Bone-Head",
				position : new Vector3f(0.0, 0.0, 5.0),
				orientation : ExtMath.orientation(0, 0.64999998, 0)
			},
			head_particles : {
				bone : "Bone-Head",
				position : new Vector3f(-2.0, 0.0, 1.0),
				orientation : ExtMath.orientation(0, 0.5, 0)
			}
		}
	};

	__AttachmentPoints["Revenant"] = {
		key : "Revenant",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 25.0, 0)
			},
			spell_origin : {
				bone : "Back02"
			},
			spell_target : {
				bone : "Back02"
			},
			spell_target_head : {
				bone : "Head"
			},
			horde_caster : {
				bone : "Back02",
				position : new Vector3f(2.0, 0.0, 3.0),
				orientation : ExtMath.orientation(0, 0.1, 0)
			},
			horde_attacker : {
				bone : "L_Hand"
			},
			horde_attacker2 : {
				bone : "R_Hand"
			},
			daze_stun : {
				bone : "Head",
				position : new Vector3f(5.0, 0, 2.0)
			}
		}
	};

	__AttachmentPoints["SpikeToad"] = {
		key : "SpikeToad",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 15.0, 0)
			},
			spell_origin : {
				bone : "Bone-Neck"
			},
			spell_target : {
				bone : "Bone-Back"
			},
			spell_target_head : {
				bone : "Bone-Head"
			},
			center_hip : {
				bone : "Bone-Back"
			},
			horde_attacker : {
				bone : "Bone-Head"
			},
			horde_caster : {
				bone : "Bone-mouth01",
				position : new Vector3f(3.0, 0.0, 5.0),
				orientation : ExtMath.orientation(0, 0.2, 0)
			},
			daze_stun : {
				bone : "Bone-mouth01",
				position : new Vector3f(3.0, 0.0, -5.0),
				orientation : ExtMath.orientation(0, 0.2, 0)
			}
		}
	};

	__AttachmentPoints["Snail"] = {
		key : "Snail",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 15.0, 0)
			},
			spell_origin : {
				bone : "Neck"
			},
			spell_target : {
				bone : "Neck"
			},
			spell_target_head : {
				bone : "Head"
			},
			horde_attacker : {
				bone : "Bone-Head"
			},
			horde_caster : {
				bone : "Head",
				position : new Vector3f(3.0, 0.0, -5.0),
				orientation : ExtMath.orientation(0, 0.2, 0)
			},
			daze_stun : {
				bone : "Head",
				position : new Vector3f(3.0, 0.0, -5.0),
				orientation : ExtMath.orientation(0, 0.2, 0)
			}
		}
	};

	__AttachmentPoints["The_Lost_One"] = {
		key : "The_Lost_One",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 15.0, 0)
			},
			head_particles : {
				bone : "Cap02"
			},
			spell_target : {
				bone : "Head"
			},
			spell_origin : {
				bone : "Head"
			},
			spell_target_head : {
				bone : "Head"
			},
			horde_attacker : {
				bone : "Head"
			},
			horde_caster : {
				bone : "Head"
			},
			daze_stun : {
				bone : "Head",
				position : new Vector3f(5.0, 0, 2.0)
			}
		}
	};

	__AttachmentPoints["Dragon"] = {
		key : "Dragon",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 35.0, 0)
			},
			head_particles : {
				bone : "Bone38"
			},
			spell_target : {
				bone : "Bone38"
			},
			left_elbow : {
				bone : "Bone10"
			},
			right_elbow : {
				bone : "Bone106"
			},
			left_arm : {
				bone : "Bone10"
			},
			right_arm : {
				bone : "Bone106"
			},
			spell_origin : {
				bone : "Bone38"
			},
			spell_target_head : {
				bone : "Bone38"
			},
			horde_attacker : {
				bone : "Bone38"
			},
			horde_caster : {
				bone : "Bone80"
			},
			daze_stun : {
				bone : "Bone38",
				position : new Vector3f(0.0, 0.0, 0.0)
			}
		}
	};

	__AttachmentPoints["Lion"] = {
		key : "Lion",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 25.0, 0)
			},
			spell_target : {
				bone : "Head"
			},
			spell_origin : {
				bone : "Head"
			},
			spell_target_head : {
				bone : "Head"
			},
			horde_attacker : {
				bone : "Head"
			},
			horde_caster : {
				bone : "Head",
				position : new Vector3f(-5.0, 0.0, -7.0),
				orientation : ExtMath.orientation(0, 0.30000001, 0)
			},
			daze_stun : {
				bone : "Head",
				position : new Vector3f(-5.0, 0.0, -7.0),
				orientation : ExtMath.orientation(0, 0.30000001, 0)
			}
		}
	};

	__AttachmentPoints["Lever_Floor"] = {
		key : "Lever_Floor",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 15.0, 0)
			}
		}
	};

	__AttachmentPoints["Easter_Egg"] = {
		key : "Easter_Egg",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 15.0, 0)
			}
		}
	};

	__AttachmentPoints["Anubian_Catapult"] = {
		key : "Anubian_Catapult",
		places : {
			nameplate : {
				bone : "Parent",
				position : new Vector3f(0, 15.0, 0)
			},
			base : {
				bone : "Parent"
			},
			basket : {
				bone : "Basket"
			}
		}
	};
}