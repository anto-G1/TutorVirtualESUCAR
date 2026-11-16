
package cl.duoc.tutorvirtual.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = VerdeInstitucional,
    onPrimary = Blanco,
    primaryContainer = VerdeSuave,
    onPrimaryContainer = VerdeOscuro,

    secondary = VerdeOscuro,
    onSecondary = Blanco,
    secondaryContainer = VerdeSalvia,
    onSecondaryContainer = NegroVerdoso,

    tertiary = VerdeOscuro,
    onTertiary = Blanco,

    background = VerdeMuyClaro,
    onBackground = NegroVerdoso,

    surface = Blanco,
    onSurface = NegroVerdoso,
    surfaceContainer = VerdeSuave,
    surfaceVariant = VerdeSuave,
    onSurfaceVariant = TextoSecundario,

    outline = BordeVerde
)

private val DarkColors = darkColorScheme(
    primary = VerdeClaro,
    onPrimary = FondoOscuro,
    primaryContainer = VerdeOscuro,
    onPrimaryContainer = Blanco,

    secondary = VerdeSalvia,
    onSecondary = NegroVerdoso,
    secondaryContainer = VerdeOscuro,
    onSecondaryContainer = Blanco,

    background = FondoOscuro,
    onBackground = Blanco,

    surface = SuperficieOscura,
    onSurface = Blanco,
    surfaceContainer = VerdeOscuro,
    surfaceVariant = VerdeOscuro,
    onSurfaceVariant = VerdeClaro,

    outline = VerdeSalvia
)

@Composable
fun TutorVirtualESUCARTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
