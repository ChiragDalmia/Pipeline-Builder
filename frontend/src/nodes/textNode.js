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
          />
        ),
      },
    ],
    handles: [{ type: "source", id: "output" }],
  };

  return <BaseNode id={id} data={data} config={config} />;
};
