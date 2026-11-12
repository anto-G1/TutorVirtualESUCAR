package cl.duoc.tutorvirtual.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = AzulPrincipal,
    onPrimary = SobreAzul,
    primaryContainer = AzulSuave,
    onPrimaryContainer = SobreAzulSuave,
    secondary = GrisSecundario,
    onSecondary = SobreAzul,
    secondaryContainer = GrisSuave,
    onSecondaryContainer = TextoOscuro,
    background = FondoClaro,
    onBackground = TextoOscuro,
    surface = SuperficieClara,
    onSurface = TextoOscuro,
    surfaceVariant = GrisSuave,
    onSurfaceVariant = TextoSecundario,
    surfaceContainer = AzulSuave,
    outline = Borde
)

private val DarkColorScheme = darkColorScheme(
    primary = AzulClaro,
    onPrimary = SobreAzulSuave,
    primaryContainer = AzulPrincipal,
    onPrimaryContainer = TextoClaro,
    secondary = GrisSuave,
    onSecondary = TextoOscuro,
    secondaryContainer = GrisSecundario,
    onSecondaryContainer = TextoClaro,
    background = FondoOscuro,
    onBackground = TextoClaro,
    surface = SuperficieOscura,
    onSurface = TextoClaro,
    surfaceVariant = GrisSecundario,
    onSurfaceVariant = GrisSuave,
    surfaceContainer = GrisSecundario,
    outline = Borde
)

@Composable
fun TutorVirtualESUCARTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}