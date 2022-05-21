package com.android.blackgoku.wallhd.classes;

import android.content.SearchRecentSuggestionsProvider;

import com.android.blackgoku.wallhd.utility.WallHDConstants;

public class SuggestionProvider extends SearchRecentSuggestionsProvider {

    public SuggestionProvider() {

        setupSuggestions(WallHDConstants.AUTHORITY, WallHDConstants.MODE);

    }
}
