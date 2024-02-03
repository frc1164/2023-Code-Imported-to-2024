// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.XboxController;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Gripper;
import frc.robot.Constants.GripperC;
import edu.wpi.first.wpilibj.Timer;



public class CubePickup extends Command {
  private final Gripper m_subsystem;
  private static Timer m_timer;


  /** Creates a new CubePickup. */
  public CubePickup(Gripper subsystem) {
    m_subsystem = subsystem;
    m_timer = new Timer();

    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(m_subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_timer.start();
    m_subsystem.Intake(0);
    m_subsystem.setgripPID(0); //was -16.5
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    m_subsystem.runGripPID(m_subsystem.gripPosition());
    m_subsystem.Intake(.75);
  }
   // SmartDashboard.putBoolean("Y_BUTTON", m_controller.getYButton());
  

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_subsystem.Intake(0);
    m_timer.stop();
    m_timer.reset();
    }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
   // return m_subsystem.gripPID.atSetpoint();
   if(( m_subsystem.ToFDistance() <= 200) && (Gripper.claspEncoder.getPosition() > -70) || (m_timer.get() >= 5)){
    return true;
  } else {
    return false;
  }  }
}
