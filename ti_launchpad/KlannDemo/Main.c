#include <RASLib/inc/common.h>
#include <RASLib/inc/motor.h>
#include <RASLib/inc/servo.h>
#include <RASLib/inc/time.h>

static tServo* servo = 0;
static int state = 0;

static float center = .45;
static float amplitude = .1;

static float motorSpeed = .5;

void turnServo(void) {
    float pos = 0;
    
    switch (state) {
        case 0:
            pos = center;
            break;
        case 1:
            pos = center - amplitude;
            break;
        case 2:
            pos = center;
            break;
        case 3:
            pos = center + amplitude;
            break;
    }
    
    Printf("setting servo to %f in state %d\n", pos, state);
    
    SetServo(servo, pos);
    
    state = (state + 1)%4;
}

// The 'main' function is the entry point of the program
int main(void) {
    tMotor* motor = 0;
    
    InitializeMCU();
    
    // Initialization
    servo = InitializeServo(PIN_B0);
    //motor = InitializeMotor(PIN_B7, PIN_B6, false, false);
    motor = InitializeMotor(PIN_C5, PIN_C4, false, true);
    
    SetMotor(motor, motorSpeed);
    SetServo(servo, center);
    
    CallEvery(turnServo, 0, 4.0f);
    
    while (1) {}
}
