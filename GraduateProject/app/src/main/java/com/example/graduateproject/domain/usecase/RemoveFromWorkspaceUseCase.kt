package com.example.graduateproject.domain.usecase

import com.example.graduateproject.domain.repository.WorkspaceRepository
import javax.inject.Inject

class RemoveFromWorkspaceUseCase @Inject constructor(private val repo: WorkspaceRepository) {
    suspend operator fun invoke(id: String) = repo.removeFromWorkspace(id)
}