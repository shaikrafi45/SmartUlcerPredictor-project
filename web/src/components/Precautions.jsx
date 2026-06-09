import React from 'react';

export function Precautions({ onBack }) {
    const tips = [
        "Continue eating balanced meals with fruits, vegetables, and lean protein.",
        "Stay hydrated—drink at least 2 liters of water daily.",
        "Include vitamin C (oranges, guava) and vitamin A (carrots, sweet potatoes).",
        "Keep the wound clean and monitor for changes.",
        "Avoid strenuous activity until fully healed.",
        "Get enough sleep to support immune function.",
        "Wear loose, breathable clothing around the wound.",
        "Avoid scratching or picking at scabs.",
        "Use mild soap and clean towels for hygiene.",
        "Practice gratitude or light meditation to support recovery."
    ];

    const videoGuides = [
        {
            icon: "▶️",
            title: "How to Change a Sterile Wound Dressing",
            url: "https://youtu.be/0vRFW2bsKpM?si=RjGJb6KYsKutsf_v"
        },
        {
            icon: "🍃",
            title: "Foods That Help You Heal Faster",
            url: "https://youtu.be/5Bvveo4MQxg?si=3OxlYbV_Az1jDNrw"
        },
        {
            icon: "🧼",
            title: "Hand Washing for Wound Care",
            url: "https://youtu.be/9TvIbXlb8LU?si=m27S0tvwHGvGRlGS"
        },
        {
            icon: "🏃",
            title: "How to Elevate a Swollen Limb",
            url: "https://youtu.be/GqbKehAG648?si=5IwI98xIJJ9D2mf9"
        }
    ];

    return (
        <section id="precautions-screen" className="screen active">
            <header className="top-bar">
                <button className="btn-back btn-back-dark" onClick={onBack}>
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <line x1="19" y1="12" x2="5" y2="12"></line>
                        <polyline points="12,19 5,12 12,5"></polyline>
                    </svg>
                    <span>Back</span>
                </button>
                <h1 className="header-title">Precautions</h1>
                <div style={{ width: '80px' }}></div>
            </header>
            
            <div className="content-scrollable">
                <h2 className="text-primary mt-medium font-bold">Precautions & Recovery Tips</h2>
                
                <div className="recovery-section mt-medium">
                    <div className="recovery-header">
                        <span className="status-dot green-dot">🟢</span>
                        <h3>Recovery Tips:</h3>
                    </div>
                    
                    <ul id="recovery-tips-list" className="tips-list mt-medium">
                        {tips.map((tip, idx) => (
                            <li key={idx}>{tip}</li>
                        ))}
                    </ul>
                </div>
                
                <hr className="divider mt-large" />
                
                <div className="video-guides-section mt-large">
                    <div className="video-header">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <rect x="2" y="2" width="20" height="20" rx="2.18" ry="2.18"></rect>
                            <line x1="7" y1="2" x2="7" y2="22"></line>
                            <line x1="17" y1="2" x2="17" y2="22"></line>
                            <line x1="2" y1="12" x2="22" y2="12"></line>
                            <line x1="2" y1="7" x2="7" y2="7"></line>
                            <line x1="2" y1="17" x2="7" y2="17"></line>
                            <line x1="17" y1="17" x2="22" y2="17"></line>
                            <line x1="17" y1="7" x2="22" y2="7"></line>
                        </svg>
                        <h3>Helpful Video Guides</h3>
                    </div>
                    
                    <div className="video-links-list mt-medium">
                        {videoGuides.map((guide, idx) => (
                            <a key={idx} href={guide.url} target="_blank" rel="noopener noreferrer" className="video-link-item">
                                <span className="video-icon">{guide.icon}</span>
                                <span className="video-title">{guide.title}</span>
                            </a>
                        ))}
                    </div>
                </div>
                
                <div className="spacer-bottom"></div>
            </div>
        </section>
    );
}

export default Precautions;
