"use client";

import { useState } from "react";
import { BaseNode } from "./baseNode";

export const InputNode = ({ id, data }) => {
  const [currName, setCurrName] = useState(
    data?.inputName || id.replace("customInput-", "input_")
  );
  const [inputType, setInputType] = useState(data.inputType || "Text");

  const inputStyle = {
    width: "90%",
    padding: "6px 8px",
    fontSize: "13px",
    border: "1px solid #d0d0d0",
    borderRadius: "6px",
    backgroundColor: "#ffffff",
    color: "#1e1e1e",
    outline: "none",
    transition: "border-color 0.2s ease, box-shadow 0.2s ease",
    fontFamily: "inherit",
  };

  const handleFocus = (e) => {
    e.target.style.borderColor = "#6b9bd1";
    e.target.style.boxShadow = "0 0 0 2px rgba(107, 155, 209, 0.1)";
  };

  const handleBlur = (e) => {
    e.target.style.borderColor = "#d0d0d0";
    e.target.style.boxShadow = "none";
  };

  const config = {
    title: "Input",
    fields: [
      {
        label: "Name",
        render: () => (
          <input
            type="text"
            value={currName}
            onChange={(e) => setCurrName(e.target.value)}
            style={inputStyle}
            onFocus={handleFocus}
            onBlur={handleBlur}
          />
        ),
      },
      {
        label: "Type",
        render: () => (
          <select
            value={inputType}
            onChange={(e) => setInputType(e.target.value)}
            style={{
              ...inputStyle,
              cursor: "pointer",
              appearance: "none",
              backgroundImage: `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%234a4a4a' d='M6 8L2 4h8z'/%3E%3C/svg%3E")`,
              backgroundRepeat: "no-repeat",
              backgroundPosition: "right 8px center",
              paddingRight: "28px",
            }}
            onFocus={handleFocus}
            onBlur={handleBlur}
          >
            <option value="Text">Text</option>
            <option value="File">File</option>
          </select>
        ),
      },
    ],
    handles: [{ type: "source", id: "value" }],
  };

  return <BaseNode id={id} data={data} config={config} />;
};
