package com.robotcontrol.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val robotController = RobotController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RobotControlTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RobotControlScreen(robotController)
                }
            }
        }
    }
}

@Composable
fun RobotControlScreen(robotController: RobotController) {
    var isRunning by remember { mutableStateOf(false) }
    var isAutoMode by remember { mutableStateOf(true) }
    var speed by remember { mutableStateOf(70f) }
    var statusText by remember { mutableStateOf("الروبوت جاهز") }
    
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "لوحة التحكم بالروبوت",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    scope.launch {
                        isRunning = true
                        robotController.start()
                        statusText = "الروبوت يعمل"
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00FF9D)
                )
            ) {
                Text("تشغيل")
            }

            Button(
                onClick = {
                    scope.launch {
                        isRunning = false
                        robotController.stop()
                        statusText = "الروبوت متوقف"
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF4444)
                )
            ) {
                Text("إيقاف")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    scope.launch {
                        isAutoMode = true
                        robotController.setMode("auto")
                        statusText = "وضع التشغيل التلقائي"
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isAutoMode) Color(0xFF00FF9D) else Color.Gray
                )
            ) {
                Text("الوضع التلقائي")
            }

            Button(
                onClick = {
                    scope.launch {
                        isAutoMode = false
                        robotController.setMode("manual")
                        statusText = "وضع التحكم اليدوي"
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!isAutoMode) Color(0xFF00FF9D) else Color.Gray
                )
            ) {
                Text("التحكم اليدوي")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("التحكم بالسرعة: ${speed.toInt()}")
        Slider(
            value = speed,
            onValueChange = {
                speed = it
                scope.launch {
                    robotController.setSpeed(it.toInt())
                }
            },
            valueRange = 0f..255f
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (!isAutoMode) {
            JoystickControl(
                onDirectionChanged = { direction ->
                    scope.launch {
                        robotController.move(direction)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = Color(0xFF3D3D3D),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = statusText,
                modifier = Modifier.padding(16.dp),
                color = Color(0xFF00FF9D)
            )
        }
    }
}
