"use client";

export const SubmitButton = () => {
  return (
    <div
      style={{
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        padding: "16px",
      }}
    >
      <button
        type="submit"
        style={{
          padding: "10px 24px",
          backgroundColor: "#ffffff",
          border: "1.5px solid #d0d0d0",
          borderRadius: "8px",
          fontSize: "14px",
          fontWeight: "500",
          color: "#1e1e1e",
          cursor: "pointer",
          boxShadow: "0 1px 3px rgba(0, 0, 0, 0.08)",
          transition: "all 0.2s ease",
        }}
        onMouseEnter={(e) => {
          e.currentTarget.style.backgroundColor = "#f5f5f5";
          e.currentTarget.style.boxShadow = "0 2px 6px rgba(0, 0, 0, 0.12)";
        }}
        onMouseLeave={(e) => {
          e.currentTarget.style.backgroundColor = "#ffffff";
          e.currentTarget.style.boxShadow = "0 1px 3px rgba(0, 0, 0, 0.08)";
        }}
        onMouseDown={(e) => {
          e.currentTarget.style.transform = "translateY(1px)";
        }}
        onMouseUp={(e) => {
          e.currentTarget.style.transform = "translateY(0)";
        }}
      >
        Submit
      </button>
    </div>
  );
};
