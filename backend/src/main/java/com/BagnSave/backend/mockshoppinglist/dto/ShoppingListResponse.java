package com.BagnSave.backend.mockshoppinglist.dto;

import java.util.List;

public record ShoppingListResponse(String username, List<ListSummary> lists) {

	public record ListSummary(Long listId, String listName, List<String> items) {}
}
