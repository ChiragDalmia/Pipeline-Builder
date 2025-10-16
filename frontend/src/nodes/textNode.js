"use client";

import { useState } from "react";
import { BaseNode } from "./baseNode";

export const TextNode = ({ id, data }) => {
  const [currText, setCurrText] = useState(data?.text || "{{input}}");

  const config = {
    title: "Text",
    fields: [
      {
        label: "Text",
        render: () => (
          <input
            type="text"
            value={currText}
            onChange={(e) => setCurrText(e.target.value)}
            style={{
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
            }}
            onFocus={(e) => {
              e.target.style.borderColor = "#6b9bd1";
              e.target.style.boxShadow = "0 0 0 2px rgba(107, 155, 209, 0.1)";
            }}
            onBlur={(e) => {
              e.target.style.borderColor = "#d0d0d0";
              e.target.style.boxShadow = "none";
            }}
          />
        ),
      },
    ],
    handles: [{ type: "source", id: "output" }],
  };

  return <BaseNode id={id} data={data} config={config} />;
};
