package com.example.cahier.ui

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.ink.strokes.Stroke
import com.example.cahier.R
import com.example.cahier.data.Note
import com.example.cahier.data.NoteType
import com.example.cahier.navigation.NavigationDestination
import com.example.cahier.ui.viewmodels.HomeScreenViewModel
import kotlinx.coroutines.launch

object HomeDestination : NavigationDestination {
    override val route = "home"
}

enum class AppDestinations(
    @StringRes val label: Int,
    val icon: ImageVector,
    @StringRes val contentDescription: Int
) {
    HOME(
        label = R.string.home,
        icon = Icons.Filled.Home,
        contentDescription = R.string.home
    ),
    SETTINGS(
        label = R.string.settings,
        icon = Icons.Filled.Settings,
        contentDescription = R.string.settings
    ),
}


@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun HomePane(
    navigateToCanvas: (Long) -> Unit,
    navigateToDrawingCanvas: (Long) -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel()
) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    val navigator = rememberListDetailPaneScaffoldNavigator<Note>()
    val noteList by homeScreenViewModel.noteList.collectAsState()
    val selectedNoteUIState by homeScreenViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(selectedNoteUIState.note) {
        if (false) {
            if (noteList.noteList.isNotEmpty()) {
                coroutineScope.launch {
                    noteList.noteList.first().let { defaultSelectedNote ->
                        navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, defaultSelectedNote)
                    }
                }
            } else {
                navigator.navigateTo(ListDetailPaneScaffoldRole.List)
            }
        }
    }

    BackHandler(navigator.canNavigateBack()) {
        coroutineScope.launch {
            navigator.navigateBack()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                AppDestinations.entries.forEach {
                    item(
                        icon = {
                            Icon(
                                it.icon,
                                contentDescription = stringResource(it.contentDescription)
                            )
                        },
                        label = { Text(stringResource(it.label)) },
                        selected = currentDestination == it,
                        onClick = { currentDestination = it }
                    )
                }
            },
            content = {
                ListDetailPaneScaffold(
                    directive = navigator.scaffoldDirective,
                    value = navigator.scaffoldValue,
                    listPane = {
                        ListPaneContent(
                            noteList = noteList.noteList,
                            onNoteClick = {
                                coroutineScope.launch {
                                    navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                                }
                                homeScreenViewModel.selectNote(it.id)
                            },
                            onAddNewTextNote = {
                                homeScreenViewModel.addNote { it ->
                                    navigateToCanvas(it)
                                }
                            },
                            onAddNewDrawingNote = {
                                homeScreenViewModel.addDrawingNote { noteId ->
                                    navigateToDrawingCanvas(noteId)
                                }
                            },
                            onDeleteNote = { note ->
                                homeScreenViewModel.deleteNote()
                                navigateUp()
                            },
                            onToggleFavorite = { noteId ->
                                homeScreenViewModel.toggleFavorite(noteId)
                            }
                        )
                    },
                    detailPane = {
                        selectedNoteUIState.note.let { note ->
                            DetailPaneContent(
                                note = note,
                                strokes = selectedNoteUIState.strokes,
                                onClickToEdit = {
                                    if (note.type == NoteType.TEXT) {
                                        navigateToCanvas(note.id)
                                    } else {
                                        navigateToDrawingCanvas(note.id)
                                    }
                                }
                            )
                        }
                    }
                )
            }
        )
    }
}

@Composable
private fun ListPaneContent(
    noteList: List<Note>,
    onNoteClick: (Note) -> Unit,
    onAddNewTextNote: () -> Unit,
    onAddNewDrawingNote: () -> Unit,
    onToggleFavorite: (Long) -> Unit,
    modifier: Modifier = Modifier,
    onDeleteNote: (Note) -> Unit = {},
) {
    val (favorites, others) = noteList.partition { it.isFavorite }

    NoteList(
        noteList = noteList,
        favorites = favorites,
        otherNotes = others,
        onNoteClick = onNoteClick,
        onAddNewTextNote = onAddNewTextNote,
        onAddNewDrawingNote = onAddNewDrawingNote,
        onDeleteNote = onDeleteNote,
        onToggleFavorite = onToggleFavorite
    )
}

@Composable
private fun DetailPaneContent(
    note: Note,
    strokes: List<Stroke>,
    onClickToEdit: (Note) -> Unit,
    modifier: Modifier = Modifier
) {
    NoteDetail(
        note = note,
        strokes = strokes,
        onClickToEdit = onClickToEdit
    )
}