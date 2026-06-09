import React from 'react';

export function ProfileModal({ isOpen, onClose, user }) {
    if (!isOpen || !user) return null;

    return (
        <div id="profile-modal" className="modal-overlay" onClick={(e) => { if (e.target.id === 'profile-modal') onClose(); }}>
            <div className="modal-card">
                <div className="modal-header">
                    <h3>User Profile</h3>
                    <button className="btn-close-modal" onClick={onClose}>&times;</button>
                </div>
                <div className="modal-body text-center">
                    <div className="avatar-large">
                        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                            <circle cx="12" cy="7" r="4"></circle>
                        </svg>
                    </div>
                    <h4 id="profile-name" className="mt-medium font-bold">{user.name}</h4>
                    <p id="profile-email" className="text-secondary">{user.email}</p>
                    <p id="profile-id" className="text-secondary text-sm mt-small">User ID: {user.id}</p>
                </div>
            </div>
        </div>
    );
}

export default ProfileModal;
