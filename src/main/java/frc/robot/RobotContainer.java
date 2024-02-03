package frc.robot;

import java.util.List;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.SwerveControllerCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import frc.robot.Constants.AutoConstants;
import frc.robot.Constants.DriveConstants;
import frc.robot.Constants.OIConstants;
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.SwerveJoystickCmd;
import frc.robot.subsystems.ArmSubsystem;
import frc.robot.subsystems.SwerveSubsystem;
import frc.robot.commands.ArmCmd;
import frc.robot.commands.BalanceCmd;
import frc.robot.commands.Clasp;
import frc.robot.commands.ConePickup;
import frc.robot.commands.CubePickup;
import frc.robot.commands.CubeInit;
import frc.robot.commands.ScoreGridTop;
import frc.robot.commands.intake;
import frc.robot.commands.output;
import frc.robot.Constants.GripperC;
import frc.robot.Constants.AutoConstants;
import frc.robot.subsystems.Gripper;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
//import com.pathplanner.lib.PathPlanner;
//import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.util.PIDConstants;
//import com.pathplanner.lib.auto.SwerveAutoBuilder;
//import com.pathplanner.lib.PathPlannerTrajectory;

import java.util.ArrayList;
import java.util.HashMap;

public class RobotContainer {

        private final SwerveSubsystem swerveSubsystem = new SwerveSubsystem();
        private final ArmSubsystem armSubsystem = new ArmSubsystem();

        private final Gripper m_gripper;
        private final CommandXboxController m_controller;

        SendableChooser<Command> m_chooser = new SendableChooser<>();

        private final SendableChooser<Command> autoChooser;
        public final HashMap<String, Command> eventMap = new HashMap<>();

        private final Joystick driverJoytick = new Joystick(OIConstants.kDriverControllerPort);
        private final XboxController armController = new XboxController(1);

        public RobotContainer() {

                m_gripper = new Gripper();
                m_controller = new CommandXboxController(OperatorConstants.kOperatorControllerPort);

                m_gripper.setDefaultCommand(new Clasp(m_gripper, m_controller));

                swerveSubsystem.setDefaultCommand(new SwerveJoystickCmd(
                                swerveSubsystem,
                                () -> driverJoytick.getRawAxis(OIConstants.kDriverYAxis),
                                () -> driverJoytick.getRawAxis(OIConstants.kDriverXAxis),
                                () -> driverJoytick.getRawAxis(OIConstants.kDriverRotAxis),
                                () -> !driverJoytick.getRawButton(OIConstants.kDriverFieldOrientedButtonIdx)));

                armSubsystem.setDefaultCommand(new ArmCmd(
                                armSubsystem,
                                armController));

                // Build an auto chooser. This will use Commands.none() as the default option.
                autoChooser = AutoBuilder.buildAutoChooser();

                //Register named commands
                NamedCommands.registerCommand("CubeInit", new CubeInit(m_gripper));
                NamedCommands.registerCommand("CubePickup", new CubePickup(m_gripper));
                NamedCommands.registerCommand("CubeOutput", new output(m_gripper));
              
                // Another option that allows you to specify the default auto by its name
                // autoChooser = AutoBuilder.buildAutoChooser("My Default Auto");

                SmartDashboard.putData("Auto Chooser", autoChooser);

                configureButtonBindings();
        }

        private void configureButtonBindings() {

                // Sets buttons
                Trigger aButton = m_controller.a();
                Trigger bButton = m_controller.b();
                Trigger yButton = m_controller.y();
                Trigger xButton = m_controller.x();
                Trigger lBumper = m_controller.leftBumper();
                Trigger rBumper = m_controller.rightBumper();
                Trigger lDPad = m_controller.povLeft();
                Trigger rDPad = m_controller.povRight();
                Trigger uDPad = m_controller.povUp();
                Trigger dDPad = m_controller.povDown();

                // keybinds
                lBumper.whileTrue(new CubePickup(m_gripper));
                rBumper.whileTrue(new ConePickup(m_gripper));
                xButton.whileTrue(new intake(m_gripper));
                aButton.whileTrue(new output(m_gripper));
                lDPad.onTrue(new ScoreGridTop(armSubsystem));

                /*
                 * new JoystickButton(driverJoytick, 2).whenPressed(() ->
                 * swerveSubsystem.zeroHeading());
                 */
                new JoystickButton(driverJoytick, 2).onTrue(new InstantCommand(() -> swerveSubsystem.zeroHeading()));
                new JoystickButton(driverJoytick, 3).onTrue(new BalanceCmd(swerveSubsystem));
        }

        public Command getAutonomousCommand() {
                return autoChooser.getSelected();
        }
}