package com.example.graduateproject.domain.usecase

import com.example.graduateproject.domain.repository.WorkspaceRepository
import javax.inject.Inject

class AddToWorkspaceUseCase @Inject constructor(private val repo: WorkspaceRepository) {
    suspend operator fun invoke(id: String) = repo.addToWorkspace(id)
}